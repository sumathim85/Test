

import mysql.connector
from mysql.connector import Error
from cryptography.fernet import Fernet

def connect(db,db_name):
    """ Connect to MySQL database """
   # print len(db["host"])
    for ip in db["host"]:
       # print ip
        conn = None
        try:
          #  print ip
            cipher_suite = Fernet(db["secret_key"] )
            Password = "'" + db["passwd"] + "'"
            mypasscode = bytes(Password, 'utf-8')
            unciphered_text = (cipher_suite.decrypt(mypasscode))
            text=unciphered_text.decode("utf-8")
            conn = mysql.connector.connect(host=ip,
                                   database=db_name,
                                       user=db["user"],
                                       password=text)
           # print("test")
            if conn.is_connected():
               # print('Connected to MySQL database')
                return conn
                break

        except Error as e:
           # print(e)
            continue
            if(len(host) == host.index(ip)):
                  print("value")
                  return conn

        #return conn

   # finally:
    #    if conn is not None and conn.is_connected():
     #       conn.close()


if __name__ == '__main__':
    connect(host)


