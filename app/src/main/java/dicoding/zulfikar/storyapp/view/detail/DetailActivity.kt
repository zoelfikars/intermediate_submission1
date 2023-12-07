package dicoding.zulfikar.storyapp.view.detail

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import dicoding.zulfikar.storyapp.data.models.StoryModel
import dicoding.zulfikar.storyapp.databinding.ActivityDetailBinding
import dicoding.zulfikar.storyapp.view.main.MainActivity


class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        setupAction()
        playAnimation()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        setupDetail()
        supportActionBar?.hide()
    }

    private fun setupDetail() {
        val receivedIntent = intent
        if (receivedIntent.hasExtra("EXTRA_STORY_MODEL")) {
            val storyModel: StoryModel? =
                receivedIntent.getParcelableExtra("EXTRA_STORY_MODEL")

            if (storyModel != null) {
                val name = storyModel.name
                val description = storyModel.description
                val url = storyModel.photoUrl
                binding.tvItemNama.text = name
                binding.tvItemDescription.text = description
                Glide.with(binding.root.context).load(url).into(binding.imgItemPhoto)
            }
        }
    }

    private fun setupAction() {
        with(binding) {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun playAnimation() {
        val toolbar = ObjectAnimator.ofFloat(binding.toolbar, View.ALPHA, 1f).setDuration(100)
        val nama = ObjectAnimator.ofFloat(binding.tvItemNama, View.ALPHA, 1f).setDuration(100)
        val deskripsi =
            ObjectAnimator.ofFloat(binding.tvItemDescription, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(toolbar, nama, deskripsi)
            startDelay = 100
        }.start()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            startActivity(Intent(this@DetailActivity, MainActivity::class.java))
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@DetailActivity, MainActivity::class.java))
        finish()
    }
}