package com.depromeet.sloth.ui.update

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.room.Update
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.ActivityUpdateLessonBinding
import com.depromeet.sloth.ui.base.BaseActivity

class UpdateLessonActivity : BaseActivity<UpdateLessonViewModel, ActivityUpdateLessonBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_lesson)
    }

    override val viewModel: UpdateLessonViewModel = UpdateLessonViewModel()

    override fun getViewBinding(): ActivityUpdateLessonBinding
        = ActivityUpdateLessonBinding.inflate(layoutInflater)

    override fun initViews() {
        super.initViews()
    }
}