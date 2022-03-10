package com.vhytron.ui.home

import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.vhytron.R
import com.vhytron.databinding.*
import com.vhytron.ui.ViewPagerAdapter
import com.vhytron.ui.chats.ChatsFragment
import com.vhytron.ui.todos.TodosFragment


class HomeFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var auth: FirebaseAuth
    private val database: DatabaseReference = Firebase.database.reference
    private val storageRef = Firebase.storage.reference.child("profileImage")
    private val ref = database.child("users").ref
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var profileBinding: ProfileAlertBinding
    private lateinit var editProfileBinding: EditProfileAlertBinding
    private lateinit var settingsBinding: SettingsAlertBinding
    private lateinit var teamsBinding: TeamsAlertBinding
    private  var imageUri: Uri? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        auth = Firebase.auth

        profileBinding = ProfileAlertBinding.inflate(layoutInflater)
        editProfileBinding = EditProfileAlertBinding.inflate(layoutInflater)
        settingsBinding = SettingsAlertBinding.inflate(layoutInflater)
        teamsBinding = TeamsAlertBinding.inflate(layoutInflater)

        homeViewModel.update()
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().setActionBar(binding.toolbar)

        //profile alert
        val builder = AlertDialog.Builder(context, R.style.WrapContentDialog)
        builder.setView(profileBinding.root)
        val profileAlert = builder.create()
        profileAlert?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        //edit profile alert
        val editProfileBuilder = AlertDialog.Builder(context, R.style.WrapContentDialog)
        editProfileBuilder.setView(editProfileBinding.root)
        val editProfileAlert = editProfileBuilder.create()
        editProfileAlert?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        //setting alert
        val settingsBuilder = AlertDialog.Builder(context, R.style.WrapContentDialog)
        settingsBuilder.setView(settingsBinding.root)
        val settingsAlert = settingsBuilder.create()
        settingsAlert?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        //teams alert
        val teamsBuilder = AlertDialog.Builder(context, R.style.WrapContentDialog)
        teamsBuilder.setView(teamsBinding.root)
        val teamsAlert = teamsBuilder.create()
        teamsAlert?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        //set teams on click listener
        profileBinding.teams.setOnClickListener {
            teamsAlert.show()
        }

        val teamAdapter = TeamsAdapter()
        teamsBinding.teamRy.layoutManager = LinearLayoutManager(activity)
        teamsBinding.teamRy.adapter = teamAdapter
        teamAdapter.setUpTeams(DummyData.teams)
        //navigate back
        teamsBinding.back.setOnClickListener {
            teamsAlert.dismiss()
        }

        //set settings on click listener
        profileBinding.settings.setOnClickListener {
            settingsAlert.show()
        }

        //navigate back
        settingsBinding.back.setOnClickListener {
            settingsAlert.dismiss()
        }


        //set edit profile button on click listener
        profileBinding.editProfile.setOnClickListener {
            editProfileAlert.show()
        }
        
        //title array
        //create spinner adapter
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, DummyData.titles)
        editProfileBinding.titleSpinner.adapter = spinnerAdapter
        editProfileBinding.titleSpinner.onItemSelectedListener = this

        val teamSpinnerAdapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_dropdown_item, DummyData.newTeam)

        teamsBinding.teamSpinner.adapter = teamSpinnerAdapter
        teamsBinding.teamSpinner.onItemSelectedListener = this

        binding.profilePic.setOnClickListener {
            profileAlert.show()
        }



        val adapter = ViewPagerAdapter(childFragmentManager, lifecycle)
        adapter.addFragment(TodosFragment(), "Todos")
        adapter.addFragment(ChatsFragment(), "Chats")
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabs, binding.viewPager){ tab, position ->
            tab.text = adapter.getPageTitle(position)
            binding.viewPager.setCurrentItem(tab.position, true)
        }.attach()

        profileBinding.logOutBt.setOnClickListener {
            auth.signOut()
            findNavController().navigate(R.id.action_nav_home_to_login)
            profileAlert.dismiss()
        }

        val getContent = registerForActivityResult(ActivityResultContracts.GetContent())  { uri: Uri? ->
            // Handle the returned Uri
            imageUri = uri
            if (uri != null) {
                uploadImageToFirebase(uri)
            }
        }


        editProfileBinding.editImage.setOnClickListener {
            getContent.launch("image/*")

        }

        editProfileBinding.saveBt.setOnClickListener {
//            editProfile((editProfileBinding.profilePic.drawable as BitmapDrawable).bitmap)
            editProfile(editProfileBinding.profileName.text.toString(),
                editProfileBinding.titleSpinner.selectedItem.toString())

            homeViewModel.update()
            editProfileAlert.dismiss()
        }

        homeViewModel.title.observe(viewLifecycleOwner){
            profileBinding.title.text = it
        }
        homeViewModel.name.observe(viewLifecycleOwner){
            profileBinding.profileName.text = it
            editProfileBinding.profileName.setText(it)
        }
        homeViewModel.userName.observe(viewLifecycleOwner){
            profileBinding.userName.text = it
        }
        homeViewModel.image.observe(viewLifecycleOwner){
            profileBinding.profilePic.setImageBitmap(it)
            editProfileBinding.profilePic.setImageBitmap(it)
            binding.profilePic.setImageBitmap(it)
        }
        homeViewModel.error.observe(viewLifecycleOwner){
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }

    }

    private fun uploadImageToFirebase(fileUri: Uri) {
        editProfileBinding.imageLoading.visibility = VISIBLE
        ref.child(auth.currentUser?.uid.toString()).child("userName")
            .get().addOnSuccessListener{ user ->
                val fileName = "${user.value.toString()}.jpg"
                val refStorage = storageRef.child(fileName)

                refStorage.putFile(fileUri)
                    .addOnSuccessListener {
                        val oneMegaByte: Long = 1024 * 1024
                        it.storage.getBytes(oneMegaByte).addOnSuccessListener { bytes ->
                            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                            editProfileBinding.profilePic.setImageBitmap(bmp)
                            editProfileBinding.imageLoading.visibility = GONE
                        }
                    }
                    .addOnFailureListener { e ->
                        print(e.message)
                    }
            }

    }
    
    private fun editProfile(name: String, title: String){
        val userRef = ref.child("${auth.currentUser.let { it?.uid }}")
        userRef.child("name").setValue(name)
        userRef.child("title").setValue(title)
            .addOnSuccessListener {
                Toast.makeText(context, "user updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "update fail", Toast.LENGTH_SHORT).show()

            }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}
}