import sys
from datetime import date, datetime, timedelta
import os
import time
import mysql.connector
import json
from .prodapt_mysql_connector import connect
import logstash
import logging
from mysql.connector import Error
from elasticsearch import Elasticsearch
import requests
from datetime import datetime, timedelta
from pytz import timezone
import pytz
from datetime import datetime, timedelta


class UpdateDB(object):
    def __init__(self, stdout=None, stderr=None):
        self._order_id = None
        self._intake_id = None
        self._suite_id = None
        self._suborder_id = "0"
        self._role = None
        self._db_name=None
        # proppath = (os.path.dirname(os.path.realpath(__file__)))
        # file = open(proppath + '/db_properties.json', 'r')
        # self._db = json.load(file)

    def set_Id(self):
        try:
            if (self._order_id == None):
                arguments = sys.argv[1:]
                indices = [i for i, s in enumerate(arguments) if 'Id' in s]
                indices_env = [i for i, s in enumerate(arguments) if 'env' in s]
                if not indices:
                    pass
                else:
                    list_id = arguments[indices[0]]
                    list_split = list_id.split(":")
                    self._order_id = list_split[1]
                    if not indices_env:
                        pass
                    else:
                        list_env = arguments[indices_env[0]]
                        list_split_env = list_env.split(":")
                        self._env=list_split_env[1]
                        proppath = (os.path.dirname(os.path.realpath(__file__)))
                        file = open(proppath + '/'+self._env.lower()+'_properties.json', 'r')
                        self._db = json.load(file)
                        self.get_role(self._order_id)
        except Error as e:
            print("Error at order information getting")
            return "error"

    def get_role(self,id):
        try:
            connection = connect(self._db)
            cursor = connection.cursor()
            cursor.execute("SELECT role_id FROM order_details WHERE order_id='" + id + "'")
            myresult = cursor.fetchall()
            for row in myresult:
                #print(row[0])
                role_id = row[0]
            cursor.execute("SELECT role_code FROM af_role WHERE role_id="+str(role_id))
            #cursor.execute("SELECT role_code FROM af_role WHERE role_id='" +role_id+ "'")
            myresult1 = cursor.fetchall()
            for rows in myresult1:
                self._role = rows[0]
            if self._role == None:
                return "Error"
            return self._role
        except Error as e:
            print("Error reading role from MySQL table", e)
            return "error"
        finally:
            if (connection.is_connected()):
                connection.close()
                cursor.close()


    def get_role_id(self,id):
        try:
            connection = connect(self._db)
            cursor = connection.cursor()
            cursor.execute("SELECT role_id FROM order_details WHERE order_id='" + id + "'")
            myresult = cursor.fetchall()
            for row in myresult:
                #print(row[0])
                role_id = row[0]
            return role_id
        except Error as e:
            print("Error reading role from MySQL table", e)
            return "error"
        finally:
            if (connection.is_connected()):
                connection.close()
                cursor.close()

    def get_user_role(self,id):
        self.set_Id()
        value = self.get_role(id)
        return value

    def get_user_role_id(self,id):
        self.set_Id()
        value = self.get_role_id(id)
        return value

    def intake_id(self, robot):
        try:
            connection = connect(self._db)
            cursor = connection.cursor()
            cursor.execute("SELECT intake_id,suite_id FROM suite_catalogue WHERE suite_name='" + robot.name + "'")
            myresult = cursor.fetchall()
            for row in myresult:
                self._intake_id = row[0]
                self._suite_id = row[1]
                #print(self._intake_id)
                #print(self._suite_id)
        except Error as e:
            print("Error reading data from MySQL table", e)
        finally:
            if (connection.is_connected()):
                connection.close()
                cursor.close()

    def set_suborder_id(self):
        if (self._suborder_id == "0"):
            arguments = sys.argv[1:]
            indices = [i for i, s in enumerate(arguments) if 'suborder_id' in s]
            if not indices:
                print("Sub Order Id is not passed to robot")
            else:
                list_id = arguments[indices[0]]
                list_split = list_id.split(":")
                self._suborder_id = list_split[1]

    def log_ur_data(self, exe="default"):
        host = self._db['logstash']
        test_logger = logging.getLogger('python-logstash-logger')
        test_logger.setLevel(logging.DEBUG)
        if not test_logger.handlers:
            test_logger.addHandler(logstash.TCPLogstashHandler(host, 5959, version=1))
        extra = {
            'order_id': self._order_id,
            'Extra': "demo",
            'log_level': "INFO",
            'role':self._role
        }
        return test_logger.debug, extra

    def updateelastic(self,robot ,savings):
        try:
            elastic=self._db['elasticIp']
            es = Elasticsearch(
                elastic,
                port=80
            )
            search_prams = {
                "query": {
                    "bool": {
                        "filter": {
                            "script": {
                                "script": {
                                    "source": "doc['Intake_Id.keyword'].value == '" + str(
                                        self._intake_id) + "' && doc['bot_name.keyword'].value == '" + robot.name + "'",
                                    "lang": "painless"
                                }
                            }
                        }
                    }
                }
            }

            response = es.search(index="rogersdashboard", body=search_prams)
            # id = response['hits']['hits'][0]['_id']
            # print(id)
            now = datetime.utcnow()
            response['hits']['hits'][0]['_source']['updated_time'] = now
            response['hits']['hits'][0]['_source']['actual_run'] = 1
            response['hits']['hits'][0]['_source']['doc_type'] = 'run'
            response['hits']['hits'][0]['_source']['Savings'] = savings
            response['hits']['hits'][0]['_source']['running_month']=now.strftime("%m(%b)-%Y")
            doc = response['hits']['hits'][0]['_source']
            es.index(index="rogersdashboard", body=doc)
        except Exception as e:
            print(e)
            print("Not able upadate actual savings in Dashboard")

    def update_db(self, robot, Status, name):

        try:
            conn =connect(self._db)
            cursor = conn.cursor()
            s, ms = divmod(robot.elapsedtime, 1000)
            elapsed_time = (time.strftime('%H:%M:%S', time.gmtime(s)))
            if (name == "testcase"):
                if (Status == "Inprogress"):
                    start_time = datetime.strptime(robot.starttime, '%Y%m%d %H:%M:%S.%f')
#                     utc_start_time = start_time.astimezone(pytz.utc)
                    if not robot.tags:
                        tags = "NULL"
                    else:
                        tags = robot.tags[0]
                    values_to_insert = {"Status": Status, "Crit": str(robot.critical),
                                        "Tags": tags, "Start_Time": start_time, "Bot_Name": robot.name,
                                        "Id": self._order_id, "suborderid": self._suborder_id}
                    command = ("UPDATE bot_details "
                               "SET status = %(Status)s, start_time = %(Start_Time)s, is_critical = %(Crit)s, tags = %(Tags)s "
                               "where order_id = %(Id)s AND bot_name = %(Bot_Name)s AND sub_order_id = %(suborderid)s")
                else:
                    if not robot.message:
                        msg = "NULL"
                    else:
                        msg = robot.message
                    end_time = datetime.strptime(robot.endtime, '%Y%m%d %H:%M:%S.%f')
#                     utc_end_time = end_time.astimezone(pytz.utc)
                    values_to_insert = {"Status": Status, "Message": msg, "Elapsed": elapsed_time,
                                        "End_Time": end_time, "Bot_Name": robot.name, "Id": self._order_id,
                                        "suborderid": self._suborder_id}
                    command = ("UPDATE bot_details "
                               "SET status = %(Status)s, end_time  = %(End_Time)s ,elapsed = %(Elapsed)s, message = %(Message)s"
                               "where order_id = %(Id)s AND bot_name = %(Bot_Name)s  AND sub_order_id = %(suborderid)s")
                    if (Status == "FAIL"):
                        cursor.execute(
                            "SELECT no_runs,savings_per_run FROM bot_catalogue WHERE bot_name='" + robot.name + "' AND suite_id='" + str(
                                self._suite_id) + "'")
                        myresult = cursor.fetchall()
                        # runs = 0
                        for row in myresult:
                            runs = row[0] + 1
                            savingsPerRun=row[1]
                            if savingsPerRun == 0:
                                runs=row[0]

                        values = {"no_runs": runs, "Bot_Name": robot.name, "suite_id": self._suite_id}
                        com = ("UPDATE bot_catalogue "
                               "SET no_runs = %(no_runs)s "
                               "where bot_name = %(Bot_Name)s AND suite_id = %(suite_id)s")
                        cursor.execute(com, values)
                        conn.commit()
                        if savingsPerRun == 0:
                            pass
                        else:
                            self.updateelastic(robot, savingsPerRun)

            else:
                line_split = robot.full_message.splitlines()
                no_of_testcase = [int(s) for s in line_split[1].split() if s.isdigit()]
                suite_name = robot.name.replace(" ", "_")
                if ' ' not in robot.name:
                    suite_name = robot.name
                else:
                    suite_name = robot.name.replace(" ", "_")

                values_to_insert = {"Elapsed": elapsed_time, "Total": no_of_testcase[0], "Pass": no_of_testcase[1],
                                    "Fail": no_of_testcase[2], "Bot_Suites": suite_name, "Order_Status": "Completed",
                                    "Id": self._order_id, "suborderid": self._suborder_id}

                command = ("UPDATE suite_details "
                           "SET total = %(Total)s, pass = %(Pass)s, fail = %(Fail)s, elapsed = %(Elapsed)s "
                           "where order_id = %(Id)s AND suite_name = %(Bot_Suites)s AND sub_order_id = %(suborderid)s")
                cursor.execute(command, values_to_insert)
                conn.commit()

                if "." not in robot.longname or "&" in robot.longname:
                    if self._suborder_id == "0":
                        command1 = ("UPDATE order_details "
                                    "SET total = %(Total)s, pass = %(Pass)s, fail = %(Fail)s,  order_status = %(Order_Status)s, elapsed = %(Elapsed)s  "
                                    "where order_id = %(Id)s AND sub_order_id = %(suborderid)s")
                    else:
                        command1 = ("UPDATE sub_order_details "
                                    "SET total = %(Total)s, pass = %(Pass)s, fail = %(Fail)s,  order_status = %(Order_Status)s, elapsed = %(Elapsed)s  "
                                    "where order_id = %(Id)s AND sub_order_id = %(suborderid)s")
                    cursor.execute(command1, values_to_insert)
                    conn.commit()

            cursor.execute(command, values_to_insert)
            conn.commit()
            cursor.close()
            conn.close()

        except mysql.connector.Error as err:
            print("Something went wrong: {}".format(err))
            self.failDbUpdate()

        # except (mysql.connector.IntegrityError, mysql.connector.DataError) as err:
        #     print("DataError or IntegrityError")
        #     print(err)
        #     self.failDbUpdate()
        #
        # except mysql.connector.ProgrammingError as err:
        #     print("Programming Error")
        #     print(err)
        #     self.failDbUpdate()
        #
        # except mysql.connector.Error as err:
        #     print(err)
        #     self.failDbUpdate()

        except Exception as e:
            print("DB Update Error*****" + str(e))
            self.failDbUpdate()

    def failDbUpdate(self):
        #print("FAIL")
        connection = connect(self._db)
        cursor = connection.cursor()
        now = datetime.utcnow()
        # now = datetime.now()
        end_time = now.strftime("%Y%m%d %H:%M:%S")
        # now = datetime.now()
        # start_time = now.strftime("%Y%m%d %H:%M:%S")
        cursor.execute("SELECT order_created_date FROM order_details WHERE order_id='" + self._order_id + "'")
        myresult = cursor.fetchall()
        for row in myresult:
            start_time = row[0]
        statime = start_time.strftime("%Y%m%d %H:%M:%S").split()
        endtime = end_time.split()
        date1 = datetime.strptime(statime[1], '%H:%M:%S')
        date2 = datetime.strptime(endtime[1], '%H:%M:%S')
        elapsed = date2 - date1
        # elapsed = datetime.strptime(str(elapsed), '%H:%M:%S')
        # s, ms = divmod(elapsed, 1000)
        # elapsed_time = (datetime.strftime('%H:%M:%S', datetime.gmtime(s)))
        now = datetime.utcnow()
        # now = datetime.now()
        end_time_after = now.strftime("%Y-%m-%d %H:%M:%S")
        values_to_insert = {"Status": "FAIL", "Message": "backend python file issue while updating db",
                            "Id": self._order_id,
                            "suborderid": self._suborder_id, "Order_Status": "FAIL","elapsed":elapsed ,"endTime":end_time_after}
        command = ("UPDATE bot_details "
                   "SET status = %(Status)s, message = %(Message)s ,elapsed= %(elapsed)s ,end_time=%(endTime)s "
                   "where order_id = %(Id)s AND sub_order_id = %(suborderid)s")
        cursor.execute(command, values_to_insert)

        connection.commit()
        cursor.execute("SELECT COUNT(bot_id)  FROM bot_details WHERE order_id='" + self._order_id + "'")
        myresult = cursor.fetchall()
        for row in myresult:
            total = row[0]
            # print(row[0])
        values_to_insert = {"Status": "FAIL", "Message": "backend python file issue while updating db",
                            "Id": self._order_id,
                            "suborderid": self._suborder_id, "Total": total, "Pass": "0", "Fail": total,
                            "Order_Status": "Failed" ,"elapsed":elapsed}

        command1 = ("UPDATE suite_details "
                    "SET total = %(Total)s, pass = %(Pass)s, fail = %(Fail)s "
                    "where order_id = %(Id)s AND sub_order_id = %(suborderid)s")
        cursor.execute(command1, values_to_insert)
        connection.commit()
        command2 = ("UPDATE order_details "
                    "SET total = %(Total)s, pass = %(Pass)s, fail = %(Fail)s,  order_status = %(Order_Status)s , message = %(Message)s , elapsed= %(elapsed)s  "
                    "where order_id = %(Id)s AND sub_order_id = %(suborderid)s")
        cursor.execute(command2, values_to_insert)
        connection.commit()
        cursor.close()
        connection.close()


updateDB= UpdateDB()
