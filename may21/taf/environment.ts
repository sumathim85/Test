// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.



export const environment = {
    production: false,
    baseUrl: 'https://taf.oss.rogers.com/taf/',
    serviceUrl: 'https://taf-app01-prod-wlfdle.oss.rogers.com:8443/uiService',
	ldaplogin:'ldapauthentication',
    // serviceUrl: 'https://10.168.160.65:8080/ldapAuth',
    serviceUrlRogers: 'https://taf-app01-prod-wlfdle.oss.rogers.com:8443/afService',
        jiraurl:'https://reqcentral.com/browse/',
    apiendpoint:['complexityList','complexity?noOfNorthBoundInterface=','&noOfSouthBoundInterface=','/login/getUsersList','depAndDirectorList','Approver','getOrderType','complexityList','SBIList','NBIList','fterate','getuserrolefordasboard'],
        // serviceUrlRogers: 'https://10.168.160.100:8080/afService',
    // serviceUrlAdmin: 'https://10.168.160.65:8080/admin/',
    livelogStart: "https://taf-elk01-prod-wlfdle.oss.rogers.com:8443/app/kibana#/dashboard/f39334c0-e8ef-11ea-9fe9-173709a55481?embed=true&_g=(filters:!(),",
    livelogMid: "time:(from:now-365d,to:now))&_a=(description:'',filters:!(),fullScreenMode:!f,options:(hidePanelTitles:!f,useMargins:!t),",
    livelogEnd: "timeRestore:!f,title:'RAF_DEV%20Live%20Log',viewMode:view)",
    reportStart: "https://taf-elk01-prod-wlfdle.oss.rogers.com:8443/app/kibana#/dashboard/9d2a7e00-e619-11ea-9fe9-173709a55481?embed=true&_g=(filters:!(),",
    reportMid: "time:(from:now-365d,to:now))&_a=(description:'10.168.160.65%3D%3E%20Cluster%20Dev%20Env',filters:!(),fullScreenMode:!f,options:(hidePanelTitles:!f,useMargins:!t),",
    reportEnd: "timeRestore:!f,title:'RAF_DEV%20DashBoard',viewMode:view)",
    adminReportStart: "https://taf-elk01-prod-wlfdle.oss.rogers.com:8443/app/kibana#/dashboard/9d2a7e00-e619-11ea-9fe9-173709a55481?embed=true&_g=(filters:!(),",
    adminReportEnd: "time:(from:now-365d,to:now))&_a=(description:'10.168.160.65%3D%3E%20Cluster%20Dev%20Env',filters:!(),fullScreenMode:!f,options:(hidePanelTitles:!f,useMargins:!t),query:(language:kuery,query:''),timeRestore:!f,title:'RAF_DEV%20DashBoard',viewMode:view)",
    adminSchedulerReportStart: "https://taf-elk01-prod-wlfdle.oss.rogers.com:8443/app/kibana#/dashboard/523627f0-28b3-11eb-892a-0717bb95a8a8?embed=true&_g=(filters:!(),",
    adminSchedulerReportEnd: "time:(from:now-365d,to:now))&_a=(description:'',filters:!(),fullScreenMode:!f,options:(hidePanelTitles:!f,useMargins:!t),query:(language:kuery,query:''),timeRestore:!f,title:'scheduler%20dashboard',viewMode:view)",
    schedulerReportStart: "https://taf-elk01-prod-wlfdle.oss.rogers.com:8443/app/kibana#/dashboard/523627f0-28b3-11eb-892a-0717bb95a8a8?embed=true&_g=(filters:!(),",
    schedulerReportMid: "time:(from:now-365d,to:now))&_a=(description:'',filters:!(),fullScreenMode:!f,options:(hidePanelTitles:!f,useMargins:!t),",
    schedulerReportEnd: "timeRestore:!f,title:'scheduler%20dashboard',viewMode:view)",
    userDashboardStart: "https://taf-elk01-prod-wlfdle.oss.rogers.com:8443/app/kibana#/dashboard/d35cdd70-5297-11eb-aaab-cd14f52f728c?embed=true&_g=(filters:!(),refreshInterval:(pause:!t,value:0),time:(from:now-365d,to:now))&_a=(description:'New%20Customer%20Environment%20',filters:!(),fullScreenMode:!f,options:(hidePanelTitles:!f,useMargins:!t),",
    userDashboardEnd: "timeRestore:!f,title:'New%20Prod%20Rogers%20User%20Dashboard%20-%20Normal%20User',viewMode:view)",
    deptDashboardStart: "https://taf-elk01-prod-wlfdle.oss.rogers.com:8443/app/kibana#/dashboard/a5e92a00-5298-11eb-aaab-cd14f52f728c?embed=true&_g=(filters:!(),refreshInterval:(pause:!t,value:0),time:(from:now-365d,to:now))&_a=(description:'New%20customer%20Environment%20',filters:!(),fullScreenMode:!f,options:(hidePanelTitles:!f,useMargins:!t),",
    deptDashboardEnd: "timeRestore:!f,title:'New%20Prod%20Rogers%20Department%20Dashboard%20',viewMode:view)",
    executiveDashboardStart: "https://taf-elk01-prod-wlfdle.oss.rogers.com:8443/app/kibana#/dashboard/021ccdd0-5114-11eb-aaab-cd14f52f728c?embed=true&_g=(filters:!(),refreshInterval:(pause:!t,value:0),time:(from:now-365d,to:now))&_a=(description:'Customer%20Environment%20',filters:!(),fullScreenMode:!f,options:(hidePanelTitles:!f,useMargins:!t),",
    executiveDashboardEnd: "timeRestore:!f,title:'New%20Prod%20Rogers%20Executive%20Dashboard',viewMode:view)",
    adminUserDashboard: `https://taf-elk01-prod-wlfdle.oss.rogers.com:8443/app/kibana#/dashboard/7d36c270-5289-11eb-aaab-cd14f52f728c?embed=true&_a=(description:'New%20Customer%20Environment%20',filters:!(),fullScreenMode:!f,options:(hidePanelTitles:!f,useMargins:!t),query:(language:kuery,query:''),timeRestore:!f,title:'New%20Prod%20Rogers%20User%20Dashboard%20-%20admin%20dashboard',viewMode:view)&_g=(filters:!(),refreshInterval:(pause:!t,value:0),time:(from:now-365d,to:now))`,
    adminDeptDashboard: "https://taf-elk01-prod-wlfdle.oss.rogers.com:8443/app/kibana#/dashboard/a5e92a00-5298-11eb-aaab-cd14f52f728c?embed=true&_g=(filters:!(),refreshInterval:(pause:!t,value:0),time:(from:now-365d,to:now))&_a=(description:'New%20customer%20Environment%20',filters:!(),fullScreenMode:!f,options:(hidePanelTitles:!f,useMargins:!t),query:(language:kuery,query:''),timeRestore:!f,title:'New%20Prod%20Rogers%20Department%20Dashboard%20',viewMode:view)",
    adminExecutiveDashboard: "https://taf-elk01-prod-wlfdle.oss.rogers.com:8443/app/kibana#/dashboard/021ccdd0-5114-11eb-aaab-cd14f52f728c?embed=true&_g=(filters:!(),refreshInterval:(pause:!t,value:0),time:(from:now-365d,to:now))&_a=(description:'Customer%20Environment%20',filters:!(),fullScreenMode:!f,options:(hidePanelTitles:!f,useMargins:!t),query:(language:kuery,query:''),timeRestore:!f,title:'New%20Prod%20Rogers%20Executive%20Dashboard',viewMode:view)"
};



/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
