package dicoding.zulfikar.storyapp.view.main

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dicoding.zulfikar.storyapp.R
import dicoding.zulfikar.storyapp.data.models.StoryModel
import dicoding.zulfikar.storyapp.data.pref.UserPreference
import dicoding.zulfikar.storyapp.data.pref.dataStore
import dicoding.zulfikar.storyapp.data.remote.Result
import dicoding.zulfikar.storyapp.databinding.ActivityMainBinding
import dicoding.zulfikar.storyapp.databinding.StoryListBinding
import dicoding.zulfikar.storyapp.view.ViewModelFactory
import dicoding.zulfikar.storyapp.view.addstory.AddStoryActivity
import dicoding.zulfikar.storyapp.view.detail.DetailActivity
import dicoding.zulfikar.storyapp.view.welcome.WelcomeActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this@MainActivity)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var storyAdapter = StoryAdapter { selectedStory ->
            val storyModel = StoryModel(
                name = selectedStory.name,
                description = selectedStory.description,
                photo = null,
                photoUrl = selectedStory.photoUrl,
                lat = null,
                lon = null
            )
            val storyListBinding: StoryListBinding = StoryListBinding.inflate(layoutInflater)

            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this as Activity,
                    Pair(storyListBinding.imgItemPhoto, "image"),
                    Pair(storyListBinding.tvItemNama, "name"),
                    Pair(storyListBinding.tvItemDescription, "description"),
                )
            val intent = Intent(this@MainActivity, DetailActivity::class.java)
            intent.putExtra("EXTRA_STORY_MODEL", storyModel)

            startActivity(intent, optionsCompat.toBundle())
            finish()
        }

        lifecycleScope.launch {
            val result = UserPreference(applicationContext.dataStore).getSession().first().token
            if (result.isEmpty()) {
                move()
            } else {
                setupView()
                setupAction()
                playAnimation()
                setupRecyclerView(storyAdapter)
            }
        }

        binding.floatingActionButton.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
            finish()
        }
    }

    private fun move() {
        startActivity(Intent(this, WelcomeActivity::class.java))
        finish()
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
        supportActionBar?.hide()
    }

    private fun setupRecyclerView(storyAdapter: StoryAdapter) {
        binding.recyclerView.adapter = storyAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            val token = UserPreference(this@MainActivity.dataStore).getSession().first().token
            val result = viewModel.getStories(token)
            showLoading(true)
            when (result) {
                is Result.Success -> {
                    showLoading(false)
                    storyAdapter.submitList(result.data.listStory)
                }

                is Result.Error -> {
                    showLoading(false)
                    showDialog(result.exception.message.toString())
                }
            }
        }
        binding.recyclerView.adapter = storyAdapter
    }

    private fun showDialog(message: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Pemberitahuan")
            setMessage(message)
            setPositiveButton("OK", null)
            create()
            show()
        }
    }

    private fun showChoiceDialog() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Logout")
            .setMessage("Apakah Anda yakin ingin logout?")
            .setPositiveButton("Ya") { _, _ ->
                logout()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun setupAction() {
        with(binding) {
            toolbar.inflateMenu(R.menu.menu_main)
            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.logout -> {
                        showChoiceDialog()
                        true
                    }

                    else -> {
                        super.onOptionsItemSelected(it)
                        false
                    }
                }
            }
        }
    }

    private fun playAnimation() {
        val toolbar = ObjectAnimator.ofFloat(binding.toolbar, View.ALPHA, 1f).setDuration(100)
        val recyclerView =
            ObjectAnimator.ofFloat(binding.recyclerView, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(toolbar, recyclerView)
            startDelay = 100
        }.start()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun logout() {
        lifecycleScope.launch {
            UserPreference(applicationContext.dataStore).logout()
        }
        move()
    }
}