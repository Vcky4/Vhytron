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
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.vhytron.Network
import com.vhytron.R
import com.vhytron.databinding.FragmentSignUpBinding
import com.vhytron.ui.home.DummyData

class SignUpFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var _binding: FragmentSignUpBinding? = null
    private lateinit var auth: FirebaseAuth

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
                signUp(binding.emailText.text.toString(), binding.passwordText.text.toString())
            }
        }else{
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
        }

        val spinnerAdapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_dropdown_item, DummyData.titles)
        binding.postSpinner.adapter = spinnerAdapter
        binding.postSpinner.onItemSelectedListener = this
    }

    private fun textTextWatcher(){
        val watcher: TextWatcher = object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val s0 = binding.emailText.text.toString()
                val s1 = binding.passwordText.text.toString()
                val s2 = binding.cPasswordText.text.toString()
                binding.signUpBt.isEnabled = !(s0.isEmpty() || s1.isEmpty() || s2.isEmpty() || s1 != s2)

                if (s1 != s2){
                    binding.cPasswordText.error = "Password does not match"
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        }

        binding.emailText.addTextChangedListener(watcher)
        binding.passwordText.addTextChangedListener(watcher)
        binding.cPasswordText.addTextChangedListener(watcher)
    }

    private fun signUp(email: String, password: String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isComplete){
                    Log.d(TAG, "createUserWithEmail:success")
                    //display successful
                    Toast.makeText(context, "Sign up successful", Toast.LENGTH_SHORT).show()
                    //navigate to home
                    findNavController().navigate(R.id.action_sign_up_to_nav_home)
                }else{
                    //if sign in fails, display message to user
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