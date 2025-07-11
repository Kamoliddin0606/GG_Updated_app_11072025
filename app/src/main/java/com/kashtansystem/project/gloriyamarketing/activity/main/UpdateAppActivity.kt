package com.kashtansystem.project.gloriyamarketing.activity.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.FileProvider
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.kashtansystem.project.gloriyamarketing.BuildConfig
import com.kashtansystem.project.gloriyamarketing.R
import com.kashtansystem.project.gloriyamarketing.core.BaseKActivity
import com.kashtansystem.project.gloriyamarketing.utils.formatted
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_update.*
import java.io.File
import java.util.*
import android.Manifest.permission

class UpdateAppActivity : BaseKActivity() {
    var versionCode = -1
    var storage = FirebaseStorage.getInstance()
    var storageRef = storage.reference
    var pathPrefix = if (BuildConfig.DEBUG) "debug/" else ""
    var versionSign = if (BuildConfig.DEBUG) "debugV" else "v"

    companion object {
        const val ACTUAL_VERSION = "ACTUAL_VERSION"
        fun instance(context: Context, versionCode: Int): Intent {
            val intent = Intent(context, UpdateAppActivity::class.java)
            intent.putExtra(ACTUAL_VERSION, versionCode)
            return intent
        }
    }

    @SuppressLint("SetTextI18n")
    override fun init(bundle: Bundle?) {
        versionCode = intent?.extras?.getInt(ACTUAL_VERSION) ?: throw Exception("should be init with version code. User instance static method!")
        tvVersionCode.text = getString(R.string.new_version) + " " + versionSign + versionCode.toString()
        uploadesCompositeDisposable.add(
                Single
                        .create<StorageMetadata> { emit ->
                            storageRef.child(pathPrefix + "v" + versionCode.toString() + ".apk")
                                    .metadata
                                    .addOnSuccessListener {
                                        emit.onSuccess(it)
                                    }.addOnFailureListener {
                                        emit.onError(it)
                                    }
                        }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            tvVersionDate.text = getString(R.string.date_version) + " " + Date(it.creationTimeMillis).formatted()
                        }, {
                            it.printStackTrace()
                            finish()
                        })
        )
        tvUpdate.setOnClickListener { _ ->
            RxPermissions(this)
                    .request(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe { granted ->
                        if (granted) {
                            val localFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "v" + versionCode.toString() + ".apk")
                            showLoading("Обновление...")
                            storageRef.child(pathPrefix + "v" + versionCode.toString() + ".apk").getFile(localFile).addOnSuccessListener {
                                hideLoading()

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    val apkUri = FileProvider.getUriForFile(this@UpdateAppActivity, BuildConfig.APPLICATION_ID + ".fileprovider", localFile)
                                    val intent = Intent(Intent.ACTION_INSTALL_PACKAGE)
                                    intent.data = apkUri
                                    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    startActivity(intent)
                                } else {
                                    val apkUri = Uri.fromFile(localFile)
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                }
                            }.addOnFailureListener {
                                hideLoading()
                                it.printStackTrace()
                                finish()
                            }
                        } else {
                            finish()
                        }
                    }

        }
        tvLater.setOnClickListener {
            finish()
        }
    }

    override fun getLayoutResource(): Int = R.layout.activity_update

    override fun initialSupportActionBar(): Boolean = true

    override fun getActionBarTitle(): String = getString(R.string.update_app)

    override fun getHomeButtonEnable(): Boolean = false
    override fun checkLogin(): Boolean {
        return false
    }
}