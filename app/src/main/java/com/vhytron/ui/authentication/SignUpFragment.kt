package com.vhytron.ui.authentication

import android.content.ContentValues.TAG
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.vhytron.Network
import com.vhytron.R
import com.vhytron.databinding.FragmentSignUpBinding
import com.vhytron.ui.chats.PeopleModel
import com.vhytron.ui.home.DummyData

class SignUpFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var _binding: FragmentSignUpBinding? = null
    private lateinit var auth: FirebaseAuth
    private val database: DatabaseReference = Firebase.database.reference
    private val storageRef = Firebase.storage.reference.child("profile")


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        // Initialize Firebase Auth
        auth = Firebase.auth
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textTextWatcher()



        binding.loginTxBt.setOnClickListener {
            findNavController().navigate(R.id.action_sign_up_to_login)
        }
        if (Network(activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as
                    ConnectivityManager
            ).isNetworkAvailable()){
            binding.signUpBt.setOnClickListener {
                if (binding.postSpinner.selectedItemPosition == 0){
                    binding.postSpinner.requestFocus()
                    Toast.makeText(context, "please choose a job tile", Toast.LENGTH_LONG).show()
                }else{
                    signUp(binding.emailText.text.toString(), binding.passwordText.text.toString())
                    binding.signUpLoading.visibility = VISIBLE
                    binding.signUpBt.isEnabled = false
                }
            }
        }else{
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
        }

        val spinnerAdapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_dropdown_item, DummyData.titles)
        binding.postSpinner.adapter = spinnerAdapter
        binding.postSpinner.onItemSelectedListener = this
    }


    private fun addUser(name: String, userName: String, title: String, userId: String) {
        //            _key.ifEmpty {
//            database.child("devotional").push().key
//        }

        val user = PeopleModel(R.drawable.profile.toDrawable().toBitmap(), name,title,userName)
        val postValues = user.toMap()

        val childUpdates = hashMapOf<String, Any>(
            "/users/$userId" to postValues
        )

        database.updateChildren(childUpdates)
            .addOnSuccessListener {
                // Write was successful!
                Toast.makeText(context, "new user add", Toast.LENGTH_SHORT).show()
                //navigate to home
                findNavController().navigate(R.id.action_sign_up_to_nav_home)
                // ...
            }
            .addOnFailureListener {
                // Write failed
                binding.signUpLoading.visibility = GONE
                binding.signUpBt.isEnabled = true
                FirebaseAuth.getInstance().currentUser?.delete()
                Toast.makeText(context, it.localizedMessage, Toast.LENGTH_LONG).show()
                // ...
            }

    }

    
    private fun textTextWatcher(){
        val watcher: TextWatcher = object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val s0 = binding.emailText.text.toString()
                val s1 = binding.passwordText.text.toString()
                val s2 = binding.cPasswordText.text.toString()
                val s3 = binding.userNameText.text.toString()
                val s4 = binding.nameText.text.toString()
                binding.signUpBt.isEnabled = !(s0.isEmpty() || s1.isEmpty() || s2.isEmpty() ||
                        s1 != s2 || s3.isEmpty() || s4.isEmpty())

                if (s1 != s2){
                    binding.cPasswordText.error = "Password does not match"
                }

                if (s3.isEmpty()){
                    binding.userNameText.error = "Please choose a userName"
                }
                if (s4.isEmpty()){
                    binding.userNameText.error = "Field must not be empty"
                }

            }

            override fun afterTextChanged(p0: Editable?) {}
        }

        binding.emailText.addTextChangedListener(watcher)
        binding.passwordText.addTextChangedListener(watcher)
        binding.cPasswordText.addTextChangedListener(watcher)
        binding.nameText.addTextChangedListener(watcher)
        binding.userNameText.addTextChangedListener(watcher)
    }

    private fun signUp(email: String, password: String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    Log.d(TAG, "createUserWithEmail:success")
                    //save user
                    auth.currentUser?.let {
                        addUser(binding.nameText.text.toString(), binding.userNameText.text.toString(),
                            binding.postSpinner.selectedItem.toString(), it.uid)
                    }
                    //display successful
                    Toast.makeText(context, "Sign up successful", Toast.LENGTH_SHORT).show()
                }else{
                    //if sign in fails, display message to user
                    binding.signUpLoading.visibility = GONE
                    binding.signUpBt.isEnabled = true
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener{exception ->
                Toast.makeText(context, exception.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {}

    override fun onNothingSelected(p0: AdapterView<*>?) {}

}