package com.kashtansystem.project.gloriyamarketing.utils

import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.kashtansystem.project.gloriyamarketing.BuildConfig
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class UpdateChecker {
    var storage = FirebaseStorage.getInstance()
    var storageRef = storage.reference
    var nextVersion = BuildConfig.VERSION_CODE + 1
    var disposable: Disposable? = null
    var pathPrefix = if(BuildConfig.DEBUG) "debug/" else ""
    interface Listener {
        fun shouldUpdate(versionCode: Int)
    }

    fun checkUpdate(listener: UpdateChecker.Listener) {
        disposable = nextVersion()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .repeat(20)
                .subscribe {
                    if (it == -1) {
                        if (nextVersion - 1 > BuildConfig.VERSION_CODE) {
                            listener.shouldUpdate(nextVersion-1)
                            disposable?.dispose()
                        } else {
                            disposable?.dispose()
                        }
                        return@subscribe
                    }
                    nextVersion++
                }
    }

    fun nextVersion(): Observable<Int> {
        return Observable.create { emit ->
            storageRef.child(pathPrefix + "v" + nextVersion.toString() + ".apk")
                    .metadata
                    .addOnSuccessListener {
                        emit.onNext(nextVersion)
                        emit.onComplete()
                    }.addOnFailureListener {
                        if (it is StorageException) {
                            if (it.errorCode == -13010) {
                                emit.onNext(-1)
                            } else {
                                emit.onError(it)
                            }
                        } else {
                            emit.onError(it)
                        }
                        emit.onComplete()
                    }
        }
    }
}