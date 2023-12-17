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
import dicoding.zulfikar.storyapp.databinding.ActivityMainBinding
import dicoding.zulfikar.storyapp.databinding.StoryListBinding
import dicoding.zulfikar.storyapp.view.MainViewModel
import dicoding.zulfikar.storyapp.view.ViewModelFactory
import dicoding.zulfikar.storyapp.view.addstory.AddStoryActivity
import dicoding.zulfikar.storyapp.view.detail.DetailActivity
import dicoding.zulfikar.storyapp.view.maps.MapsActivity
import dicoding.zulfikar.storyapp.view.welcome.WelcomeActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this@MainActivity)
    }
    private var storyAdapter = StoryPagingAdapter { selectedStory ->
        val storyModel = StoryModel(
            name = selectedStory.name,
            description = selectedStory.description,
            photo = null,
            photoUrl = selectedStory.photoUrl,
            lat = selectedStory.lat,
            lon = selectedStory.lon
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        lifecycleScope.launch {
            val result = UserPreference(applicationContext.dataStore).getSession().first().token
            if (result.isEmpty()) {
                move(Intent(this@MainActivity, WelcomeActivity::class.java))
            } else {
                setupView()
                setupAction()
                playAnimation()
                setupPaging()
//                test()
            }
        }

        binding.floatingActionButton.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
            finish()
        }
    }

    private fun move(intent: Intent) {
        startActivity(intent)
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

    private suspend fun setupPaging() {
        val adapter = storyAdapter
        binding.recyclerView.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        val token = UserPreference(this@MainActivity.dataStore).getSession().first().token
        viewModel.getStoryPaging(token).observe(this) {
            adapter.submitData(lifecycle, it)
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

                    R.id.maps -> {
                        val intent = Intent(this@MainActivity, MapsActivity::class.java)
                        val list = storyAdapter.snapshot().items
                        intent.putParcelableArrayListExtra("List", ArrayList(list))
                        move(intent)
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

    private fun logout() {
        lifecycleScope.launch {
            UserPreference(applicationContext.dataStore).logout()
        }
        move(Intent(this@MainActivity, WelcomeActivity::class.java))
    }
}