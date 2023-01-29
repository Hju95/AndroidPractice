package com.firehong.pushalarm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService: FirebaseMessagingService() {

    //사용자가 앱 삭제 후 재설치, 기기변경 등 하면 토큰값이 변경되니
    //항상 최신 토큰을 받아야한다.
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
    }

}