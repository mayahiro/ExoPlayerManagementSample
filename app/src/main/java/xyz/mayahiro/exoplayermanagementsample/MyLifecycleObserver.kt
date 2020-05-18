package xyz.mayahiro.exoplayermanagementsample

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import xyz.mayahiro.exoplayermanagementsample.player.PlayerManager

class MyLifecycleObserver : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        // 最初の1回のみ
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        // アプリ開始時 or バックグラウンドからの復帰時
        PlayerManager.playAllPlayer()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        // アプリ開始時 or バックグラウンドからの復帰時
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        // アプリ終了時 or バックグラウンド移行時
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        // アプリ終了時 or バックグラウンド移行時
        PlayerManager.pauseAllPlayer()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        // 呼ばれない
    }
}
