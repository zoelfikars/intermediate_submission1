package dicoding.zulfikar.storyapp.view.addstory

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Switch
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import dicoding.zulfikar.storyapp.R
import dicoding.zulfikar.storyapp.data.pref.UserPreference
import dicoding.zulfikar.storyapp.data.pref.dataStore
import dicoding.zulfikar.storyapp.data.remote.Result
import dicoding.zulfikar.storyapp.databinding.ActivityAddStoryBinding
import dicoding.zulfikar.storyapp.view.MainViewModel
import dicoding.zulfikar.storyapp.view.ViewModelFactory
import dicoding.zulfikar.storyapp.view.addstory.CameraXActivity.Companion.CAMERAX_RESULT
import dicoding.zulfikar.storyapp.view.main.MainActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private var currentImageUri: Uri? = null
    private var isLocationEnabled = false
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }


    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERAX_RESULT) {
            currentImageUri = it.data?.getStringExtra(CameraXActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
            showImage()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    val latitude: Double = location.latitude
                    val longitude: Double = location.longitude
                }
            }
        }
        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }
        lifecycleScope.launch {
            val result = UserPreference(applicationContext.dataStore).getSession().first().token
            if (result.isEmpty()) {
                move()
            } else {
                setupView()
                setupAction()
                playAnimation()
            }
        }

    }
    private fun startLocationUpdates() {
        if (allPermissionsGranted()) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } else {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }
    }
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_menu, menu)
        val locationSwitch = menu?.findItem(R.id.menu_location)?.actionView?.findViewById<Switch>(R.id.locationSwitch)
        locationSwitch?.setOnCheckedChangeListener { _, isChecked ->
            isLocationEnabled = isChecked
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestLocationPermission()
            }
            if (isLocationEnabled) {
                startLocationUpdates()
            } else {
                stopLocationUpdates()
            }
        }

        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView3, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val gallery = ObjectAnimator.ofFloat(binding.button2, View.ALPHA, 1f).setDuration(100)
        val upload =
            ObjectAnimator.ofFloat(binding.button3, View.ALPHA, 1f).setDuration(100)
        val camera =
            ObjectAnimator.ofFloat(binding.button, View.ALPHA, 1f).setDuration(100)
        val toolbar =
            ObjectAnimator.ofFloat(binding.toolbar, View.ALPHA, 1f).setDuration(100)
        val description =
            ObjectAnimator.ofFloat(binding.textInputLayout, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                gallery,
                camera,
                toolbar,
                description,
                upload,
            )
            startDelay = 100
        }.start()
    }

    private fun setupAction() {
        with(binding) {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            button.setOnClickListener {
                startCameraX()
            }
            button2.setOnClickListener {
                startGallery()
            }
            button3.setOnClickListener {
                if(isLocationEnabled) {
                    getLocationAndUpload()
                } else {
                    uploadStory(null, null)
                }
            }
        }
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

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.imageView3.setImageURI(it)
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraXActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun requestLocationPermission() {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    private fun getLocationAndUpload() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        } else {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        uploadStory(location.latitude, location.longitude)
                    }
                }
                .addOnFailureListener { exception ->
                    showToast("Gagal mendapatkan lokasi: ${exception.message}")
                }
        }
    }


    private fun uploadStory(lat: Double?, lon: Double?) {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            val description = binding.textInputEditText.text.toString()
            lifecycleScope.launch {
                showLoading(true)
                val token = UserPreference.getInstance(applicationContext.dataStore).getSession().first().token
                val result = viewModel.uploadImage(imageFile, description, lat, lon, isLocationEnabled, token)
                when (result) {
                    is Result.Success -> {
                        showLoading(false)
                        move()
                    }

                    is Result.Error -> {
                        showLoading(false)
                        showToast(result.exception.message.toString())
                    }
                }
            }
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun move() {
        showToast("upload berhasil")
        val intent = Intent(this@AddStoryActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar3.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            startActivity(Intent(this@AddStoryActivity, MainActivity::class.java))
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@AddStoryActivity, MainActivity::class.java))
        finish()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}