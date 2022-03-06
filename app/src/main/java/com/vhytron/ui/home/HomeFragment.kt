package com.vhytron.ui.home

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.net.toUri
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
import com.vhytron.R
import com.vhytron.databinding.*
import com.vhytron.ui.ViewPagerAdapter
import com.vhytron.ui.chats.ChatsFragment
import com.vhytron.ui.chats.PeopleModel
import com.vhytron.ui.todos.TodosFragment

class HomeFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var auth: FirebaseAuth
    private val database: DatabaseReference = Firebase.database.reference
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var profileBinding: ProfileAlertBinding
    private lateinit var editProfileBinding: EditProfileAlertBinding
    private lateinit var settingsBinding: SettingsAlertBinding
    private lateinit var teamsBinding: TeamsAlertBinding
    private val ref = database.child("users").ref
    private val pickImage = 1
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

        update()
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


//        this.requireActivity().setActionBar(binding.toolbar)

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
            editProfileBinding.profilePic.setImageURI(uri)
        imageUri = uri// Handle the returned Uri
        }


        editProfileBinding.editImage.setOnClickListener {
            getContent.launch("image/*")

        }

        editProfileBinding.saveBt.setOnClickListener {
//            editProfile((editProfileBinding.profilePic.drawable as BitmapDrawable).bitmap)
            val inputStream = imageUri?.let { it1 -> activity?.contentResolver?.openInputStream(it1) }
            val drawable = Drawable.createFromStream(inputStream, imageUri.toString())
            editProfile(drawable, editProfileBinding.profileName.text.toString(),
                editProfileBinding.titleSpinner.selectedItem.toString(),
            editProfileAlert)
        }

    }

//    private fun getBitmap(uri: Uri) : Bitmap?{
//        try {
//            val  parcelFileDescriptor = content
//        }
//
//    }
    
    
    private fun editProfile(image: Drawable, name: String, title: String, alertDialog: AlertDialog){
            val user = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (dataValues in dataSnapshot.children) {
                        auth.currentUser.let {
                            if (dataValues.key == it?.uid){
                                val user = PeopleModel(image, name,title,dataValues.child("userName").value.toString())
                                val postValues = user.toMap()

                                val childUpdates = hashMapOf<String, Any>(
                                    "/users/${it?.uid}" to postValues
                                )

                                database.updateChildren(childUpdates)
                                    .addOnSuccessListener {
                                        // Write was successful!
                                        Toast.makeText(context, "new user add", Toast.LENGTH_SHORT).show()
                                        //navigate to home
                                        editProfileBinding.saveLoading.visibility = View.GONE
                                        editProfileBinding.saveBt.isEnabled = true
                                        alertDialog.dismiss()
                                        // ...
                                    }
                                    .addOnFailureListener { exception ->
                                        // Write failed
                                        editProfileBinding.saveLoading.visibility = View.GONE
                                        editProfileBinding.saveBt.isEnabled = true
                                        Toast.makeText(context, exception.localizedMessage, Toast.LENGTH_LONG).show()
                                        // ...
                                    }
                            }
                        }
                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // handle error
                    editProfileBinding.saveLoading.visibility = View.GONE
                    Toast.makeText(context, "unable to update events", Toast.LENGTH_SHORT).show()

                }
            }
            ref.addListenerForSingleValueEvent(user)


    }


    @SuppressLint("SetTextI18n")
    private fun update(){
        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val menuListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (dataValues in dataSnapshot.children) {
                            auth.currentUser.let {
                                if (it != null) {
                                    if (dataValues.key.toString() == it.uid){
                                        val userName = dataValues.child("userName").value.toString()
                                        val image = dataValues.child("image").value.toString().toInt().toDrawable()
                                        val title = dataValues.child("title").value.toString()
                                        val name = dataValues.child("name").value.toString()

                                        binding.profilePic.setImageDrawable(image)
                                        profileBinding.profilePic.setImageDrawable(image)
                                        editProfileBinding.profilePic.setImageDrawable(image)
                                        profileBinding.profileName.text = name
                                        profileBinding.userName.text = userName
                                        profileBinding.title.text = title


                                        homeViewModel.addDetails(name, userName, title, image)
                                    }
                                }
                            }
                        }

                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // handle error
                    }
                }
                ref.addListenerForSingleValueEvent(menuListener)            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}

        }

        ref.addChildEventListener(childEventListener)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}
}