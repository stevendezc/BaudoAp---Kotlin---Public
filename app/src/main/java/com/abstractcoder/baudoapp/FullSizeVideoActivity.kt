package com.abstractcoder.baudoapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.core.view.GestureDetectorCompat
import com.abstractcoder.baudoapp.databinding.ActivityFullSizeVideoBinding
import com.abstractcoder.baudoapp.recyclers.VideoPostMain

class FullSizeVideoActivity : AppCompatActivity(), GestureDetector.OnGestureListener {

    private lateinit var binding: ActivityFullSizeVideoBinding
    private lateinit var videoContent: VideoPostMain

    // Gesture detector
    private lateinit var gestureDetector: GestureDetectorCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullSizeVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        videoContent = intent.getParcelableExtra<VideoPostMain>("video")!!

        gestureDetector = GestureDetectorCompat(this, this)

        setup(videoContent!!)
    }

    private fun setup(videoContent: VideoPostMain) {
        if (videoContent != null) {
            val videoView = binding.fullSizeVideoView

            videoView.setVideoURI(videoContent.video)
            videoView.requestFocus()
            videoView.setOnPreparedListener {
                it.start()
                it.setOnCompletionListener {
                    finish()
                }
            }

            binding.fullSizeVideoBack.setOnClickListener {
                finish()
            }

        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event!!) || super.onTouchEvent(event)
    }

    override fun onDown(e: MotionEvent): Boolean {
        return true
    }

    override fun onShowPress(e: MotionEvent) {}

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return false
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent) {}

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        if (e2.y < e1.y) {
            // Swipe from bottom to top detected
            // Start the new activity here
            val intent = Intent(this.baseContext, InnerVideoContentActivity::class.java)
            intent.putExtra("video_content", videoContent)
            startActivity(intent)
            overridePendingTransition(com.facebook.R.anim.abc_slide_in_bottom, com.facebook.R.anim.abc_slide_out_top)
            return true
        }
        return false
    }

}