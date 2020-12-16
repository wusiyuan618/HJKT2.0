package com.wusy.serialportproject.bean

class RegisteBean {

    /**
     * status : 0
     * msg : 成功
     * data : {"userList":[{"id":"1","userName":"aaa","pwd":"123456","mac":"00:E0:99:00:6B:3E","sex":"1","status":"0"},{"id":"2","userName":"bbb","pwd":"123456","mac":"00:E0:99:00:6B:3E","sex":"1","status":"0"}],"loginCode":"1f47d8"}
     */

    var status: String? = null
    var msg: String? = null
    var data: DataBean? = null

    class DataBean {
        /**
         * userList : [{"id":"1","userName":"aaa","pwd":"123456","mac":"00:E0:99:00:6B:3E","sex":"1","status":"0"},{"id":"2","userName":"bbb","pwd":"123456","mac":"00:E0:99:00:6B:3E","sex":"1","status":"0"}]
         * loginCode : 1f47d8
         */

        var loginCode: String? = null
        var userList: List<UserListBean>? = null

        class UserListBean {
            /**
             * id : 1
             * userName : aaa
             * pwd : 123456
             * mac : 00:E0:99:00:6B:3E
             * sex : 1
             * status : 0
             */

            var id: String? = null
            var userName: String? = null
            var pwd: String? = null
            var mac: String? = null
            var sex: String? = null
            var status: String? = null
            var isOpen=false
        }
    }
}
