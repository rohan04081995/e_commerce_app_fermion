package com.example.fermiontask.ui

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.CursorWindow
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.fermiontask.BuildConfig
import com.example.fermiontask.R
import com.example.fermiontask.db.UsersDatabase
import com.example.fermiontask.model.ProfileModel
import java.io.File
import java.lang.reflect.Field
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


class ProfileActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var user_profile_iv: ImageView
    private lateinit var add_image_ib: ImageButton
    private lateinit var user_phone_no_et: EditText
    private lateinit var user_email_et: EditText
    private lateinit var user_address_et: EditText
    private lateinit var submit_user_details_button: Button
    private lateinit var profile_toolbar: androidx.appcompat.widget.Toolbar

    private lateinit var storagePermissionLauncher: ActivityResultLauncher<String>
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>

    private lateinit var settingsLauncher: ActivityResultLauncher<Intent>

    private lateinit var cameraMultiplePermissionsLauncher: ActivityResultLauncher<Array<String>>

    //    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>

    private var my_bitmap: Bitmap? = null

    var photoFile: File? = null
    var mCurrentPhotoPath: String? = null


    val MOBILE_NO_PATTERN = Pattern.compile("[6-9][0-9]{9}")
    val ALL_DIGITS_SAME_PATTERN = Pattern.compile("^([0-9])\\1*$")

    private lateinit var userDatabase: UsersDatabase
    private var profileModel_retrieved: ProfileModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        try {
            val field: Field = CursorWindow::class.java.getDeclaredField("sCursorWindowSize")
            field.isAccessible = true
            field.set(null, 100 * 1024 * 1024) //the 100MB is the new size
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        userDatabase = UsersDatabase(this)

        profile_toolbar = findViewById(R.id.profile_toolbar)
        setSupportActionBar(profile_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(resources.getDrawable(R.drawable.ic_arrow_back_))

        supportActionBar?.title = "Profile"
        profile_toolbar.setTitleTextColor(
            ContextCompat.getColor(
                this, R.color.white
            )
        )

        user_profile_iv = findViewById(R.id.user_profile_iv)
        add_image_ib = findViewById(R.id.add_image_ib)
        user_phone_no_et = findViewById(R.id.user_phone_no_et)
        user_email_et = findViewById(R.id.user_email_et)
        user_address_et = findViewById(R.id.user_address_et)
        submit_user_details_button = findViewById(R.id.submit_user_details_button)

        user_profile_iv.setOnClickListener(this)
        add_image_ib.setOnClickListener(this)
        submit_user_details_button.setOnClickListener(this)

        profileModel_retrieved = userDatabase.getUser()

        if (profileModel_retrieved != null) {
            user_phone_no_et.setText(profileModel_retrieved?.phoneNumber)
            user_email_et.setText(profileModel_retrieved?.emailAddress)
            user_address_et.setText(profileModel_retrieved?.deliveryAddress)
            my_bitmap = profileModel_retrieved?.bitmap
            my_bitmap.let {
                user_profile_iv.setImageBitmap(my_bitmap)
                add_image_ib.visibility = View.GONE
            }
        }

        if (my_bitmap == null) {
            add_image_ib.visibility = View.VISIBLE
        }

        storagePermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {

                if (it) {
                    pickImageFromGallery()
                } else {
                    var permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        android.Manifest.permission.READ_MEDIA_IMAGES
                    } else {
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    }

                    if (!shouldShowRequestPermissionRationale(permission)) {
                        showPermissionExplanationDialog("storage permission is needed for get photo from gallery")
                    }

                }
            }
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                add_image_ib.visibility = View.GONE
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(contentResolver, uri)
                    my_bitmap = ImageDecoder.decodeBitmap(source)
                    user_profile_iv.setImageBitmap(my_bitmap)

                } else {
                    my_bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    user_profile_iv.setImageBitmap(my_bitmap)
                }
                val scaleFactor = 0.5f // scale down by 50%
                val matrix = Matrix()
                matrix.postScale(scaleFactor, scaleFactor)
                my_bitmap = Bitmap.createBitmap(
                    my_bitmap!!, 0, 0, my_bitmap!!.width, my_bitmap!!.height, matrix, true
                )
            }
        }

        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) {

            if (it) {
                add_image_ib.visibility = View.GONE
                my_bitmap = BitmapFactory.decodeFile(photoFile!!.absolutePath)
                val scaleFactor = 0.7f // scale down by 50%
                val matrix = Matrix()
                matrix.postScale(scaleFactor, scaleFactor)
                my_bitmap = Bitmap.createBitmap(
                    my_bitmap!!, 0, 0, my_bitmap!!.width, my_bitmap!!.height, matrix, true
                )

                user_profile_iv.setImageBitmap(my_bitmap)
            }
        }


        settingsLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
        }

        cameraMultiplePermissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {

                val cameraPermission = it[android.Manifest.permission.CAMERA] ?: false
                var storagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    it[android.Manifest.permission.READ_MEDIA_IMAGES] ?: false
                } else {
                    it[android.Manifest.permission.READ_EXTERNAL_STORAGE] ?: false
                }

                if (cameraPermission && storagePermission) {

                    openCamera()
                } else {
                    var storagePerm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        android.Manifest.permission.READ_MEDIA_IMAGES
                    } else {
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    }


                    var message: String =
                        if (!shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA) && !shouldShowRequestPermissionRationale(
                                storagePerm
                            )
                        ) {
                            "allow camera and storage permissions to capture and access photo"
                        } else if (!shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
                            "allow camera permission to capture photo"
                        } else {
                            "allow storage permission to access photo"
                        }
                    showPermissionExplanationDialog(message)
                }
            }

    }

    private fun pickImageFromGallery() {
        pickImageLauncher.launch("image/*")
    }

    private fun galleryPermissionCheck(permission: String) {
        if (ContextCompat.checkSelfPermission(
                this, permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            pickImageFromGallery()
        } else {
            storagePermissionLauncher.launch(permission)

        }
    }

    private fun cameraPermissionCheck(storagePermission: String) {

        if (ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this, storagePermission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        } else {
            cameraMultiplePermissionsLauncher.launch(
                arrayOf(
                    android.Manifest.permission.CAMERA, storagePermission
                )
            )
        }

    }

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.absolutePath
        return image
    }

    private fun displayMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    private fun openCamera() {

//        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            photoFile = createImageFile()

            val photoURI = FileProvider.getUriForFile(
                this, "${BuildConfig.APPLICATION_ID}.provider", photoFile!!
            )
//                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            cameraLauncher.launch(photoURI)

        } catch (ex: Exception) {
            displayMessage(baseContext, ex.message.toString())
        }
    }

    private fun showPermissionExplanationDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
        builder.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->

            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:$packageName")
            settingsLauncher.launch(intent)
        })
        builder.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->

        })
        val alert = builder.create()
        alert.show()
    }


    private fun validateEmailAddress(): Boolean {
        return if (user_email_et.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "please enter valid email address", Toast.LENGTH_SHORT).show()
            user_email_et.error = "please enter valid email address"
            false
        } else if (!Pattern.matches(
                Patterns.EMAIL_ADDRESS.pattern(), user_email_et.text.toString().trim()
            )
        ) {
            user_email_et.error = "please enter valid email address"
            Toast.makeText(this, "please enter valid email address", Toast.LENGTH_SHORT).show()
            false
        } else {
            user_email_et.error = null
            true
        }
    }

    private fun validatePhoneNumber(): Boolean {
        val mobile: String = user_phone_no_et.getText().toString().trim()
        return if (!MOBILE_NO_PATTERN.matcher(mobile).matches() or ALL_DIGITS_SAME_PATTERN.matcher(
                mobile
            ).matches()
        ) {
            user_phone_no_et.error = "please enter valid phone number"
            Toast.makeText(this, "please enter valid phone number", Toast.LENGTH_SHORT).show()
            false
        } else {
            user_phone_no_et.error = null
            true
        }
    }

    private fun validateAddress(): Boolean {
        return if (user_address_et.text.toString().trim().isEmpty()) {
            user_address_et.error = "please enter valid address"
            false
        } else {
            user_address_et.error = null
            true
        }
    }

    private fun validateImage(): Boolean {
        return if (my_bitmap == null) {
            Toast.makeText(this, "please add profile photo", Toast.LENGTH_SHORT).show()
            false
        } else {
            true
        }
    }


    private fun showDialog() {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialog.setTitle("select your option")
        val items = arrayOf("Gallery", "Camera")
        alertDialog.setItems(items, DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                0 -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        galleryPermissionCheck(android.Manifest.permission.READ_MEDIA_IMAGES)
                    } else {
                        galleryPermissionCheck(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
                1 -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        cameraPermissionCheck(android.Manifest.permission.READ_MEDIA_IMAGES)
                    } else {
                        cameraPermissionCheck(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
            }

        })
        val alert: AlertDialog = alertDialog.create()
        alert.show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.user_profile_iv -> showDialog()
            R.id.add_image_ib -> showDialog()
            R.id.submit_user_details_button -> {
                if (!validateEmailAddress() or !validatePhoneNumber() or !validateAddress() or !validateImage()) {
                    return
                }

                val profileModel = ProfileModel(
                    phoneNumber = user_phone_no_et.text.toString(),
                    emailAddress = user_email_et.text.toString(),
                    deliveryAddress = user_address_et.text.toString(),
                    bitmap = my_bitmap
                )

                val handlerThread = HandlerThread("DatabaseHandlerThread").apply { start() }
                val databaseHandler = Handler(handlerThread.looper)

                databaseHandler.post {
                    val message = if (profileModel_retrieved != null) {
                        userDatabase.updateUser(profileModel)
                        "profile updated"
                    } else {
                        userDatabase.addUser(profileModel)
                        "profile saved"
                    }
                    Toast.makeText(this@ProfileActivity, message, Toast.LENGTH_SHORT).show()
                }
                Toast.makeText(
                    this@ProfileActivity, "updating profile", Toast.LENGTH_SHORT
                ).show()

                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}