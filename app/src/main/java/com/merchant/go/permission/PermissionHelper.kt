package com.merchant.go.permission

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.permissionx.guolindev.PermissionMediator
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.request.PermissionBuilder

class PermissionHelper {

    private var media: PermissionMediator? = null
    private var builder: PermissionBuilder? = null
    private var contentString = "请允许快进商户使用您的相机权限"

    companion object {

        @JvmStatic
        fun with(fragmentActivity: FragmentActivity): PermissionHelper {
            val helper = PermissionHelper()
            helper.media = PermissionX.init(fragmentActivity)
            return helper
        }

        @JvmStatic
        fun with(fragment: Fragment): PermissionHelper {
            val helper = PermissionHelper()
            helper.media = PermissionX.init(fragment)
            return helper
        }

        const val CONTENT_CAMERA_FILE = "相机和储存"
        const val CONTENT_FILE = "储存"
        const val CONTENT_LOCATION = "位置"
        const val CONTENT_BLE = "位置和蓝牙"
        const val CONTENT_PHONE = "电话"
    }

    fun sdCardPer(): PermissionHelper {
        permission(Permissions.EXTERNAL_STORAGE)
        type(CONTENT_FILE)
        return this
    }

    fun cameraSDCardPer(): PermissionHelper {
        permission(Permissions.EXTERNAL_STORAGE.apply {
            add(Permissions.CAMERA)
        })
        type(CONTENT_CAMERA_FILE)
        return this
    }

    fun blePer(): PermissionHelper {
        permission(Permissions.BLE)
        type(CONTENT_BLE)
        return this
    }

    fun callPhonePer(): PermissionHelper {
        permission(Permissions.CALL_PHONE)
        type(CONTENT_PHONE)
        return this
    }

    fun locationPer(): PermissionHelper {
        permission(Permissions.LOCATION)
        type(CONTENT_LOCATION)
        return this
    }

    fun permission(permissions: List<String>): PermissionHelper {
        builder = media?.permissions(permissions)
        return this
    }

    fun permission(permission: String): PermissionHelper {
        builder = media?.permissions(permission)
        return this
    }

    fun type(contentString: String): PermissionHelper {
        this.contentString = contentString
        return this
    }

    fun request(callBack: RequestSuccess) {
        builder?.apply {
            onExplainRequestReason { scope, deniedList ->
                val dialog = PermissionDialog.with(builder!!.activity).permissions(deniedList).bind(
                    "安全提示",
                    "请允许快进商户使用您的${contentString}权限",
                    "去开启授权"
                )
                scope.showRequestReasonDialog(dialog)
                dialog.setCancelable(true)
                dialog.setCanceledOnTouchOutside(true)
            }
            onForwardToSettings { scope, deniedList ->
                val dialog = PermissionDialog.with(builder!!.activity).permissions(deniedList).bind(
                    "安全提示",
                    "请允许快进商户使用您的${contentString}权限",
                    "去开启授权"
                )
                scope.showForwardToSettingsDialog(dialog)
                dialog.setCancelable(true)
                dialog.setCanceledOnTouchOutside(true)
            }
            this.request { allGranted, grantedList, deniedList ->
                if (allGranted) callBack.onSuccess()
                //测试结果部分拒绝的时候 并不会走下面的逻辑 部分拒绝还会走上面的解释原因的代码块
//                else {
//                    PermissionDialog.with(builder!!.activity).permissions(deniedList).bind(
//                        "安全提示",
//                        "请允许快进商户使用您的${contentString}权限",
//                        "去开启授权"
//                    ).apply {
//                        positiveButton.setOnClickListener {
//                            this@PermissionHelper.permission(deniedList)
//                            this@PermissionHelper.request(callBack)
//                        }
//                        show()
//                    }
//                }
            }
        }
    }

}

