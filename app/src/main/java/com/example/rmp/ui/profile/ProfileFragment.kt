package com.example.rmp.ui.profile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.rmp.data.SessionManager
import com.example.rmp.R
import com.example.rmp.data.DatabaseHelper
import com.example.rmp.databinding.FragmentProfileBinding


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var saveChangeButton : Button
    private var previousSpinnerPosition = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val sessionManager = context?.let { SessionManager.getInstance(it) }
        val currentUserName = sessionManager?.getCurrentUser()?.userId
        val currentUser = sessionManager?.getCurrentUser()

        val currentUserTextView: TextView = binding.currentUserTextView


        val genderSpinner: Spinner = binding.genderSpinner
        val array: Array<String> = resources.getStringArray(R.array.gender_array)
        val genderArrayAdapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.gender_array,
            android.R.layout.simple_spinner_item
        )
        genderArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genderSpinner.adapter = genderArrayAdapter

        val nameEditText: EditText = binding.nameEditText
        val lastNameEditText : EditText = binding.lastNameEditText
        val dateOfBirthEditText : EditText = binding.dateOfBirthEditText
        saveChangeButton = binding.saveChangeButton

        if (currentUser != null) {
            nameEditText.setText(currentUser.userId)
            lastNameEditText.setText(currentUser.displayName)
            dateOfBirthEditText.setText(currentUser.displayBirthday)
            if (currentUser.displayGender == "Мужской")
                genderSpinner.setSelection(0)
            else
                genderSpinner.setSelection(1)
        }

        previousSpinnerPosition = genderSpinner.selectedItemPosition

        nameEditText.addTextChangedListener(textWatcher)
        lastNameEditText.addTextChangedListener(textWatcher)
        dateOfBirthEditText.addTextChangedListener(textWatcher)
        genderSpinner.onItemSelectedListener = itemSelectedListener

        if (currentUserName != null) {
            profileViewModel.setCurrentUser(currentUserName)
        }

        saveChangeButton.setOnClickListener {
            val newName = lastNameEditText.text.toString()
            val newGender = genderSpinner.selectedItem.toString()
            val newBirthdate = dateOfBirthEditText.text.toString()

            val sessionManager = context?.let { SessionManager.getInstance(it) }
            val currentUser = sessionManager?.getCurrentUser()
            val newLogin = nameEditText.text.toString()
            if (newLogin != null) {
                val dbHelper = DatabaseHelper(requireContext())
                dbHelper.updateUserInfo(newLogin, newName, newGender, newBirthdate)
                if (sessionManager != null) {
                    sessionManager.logout()
                    if (currentUser != null) {
                        sessionManager.createSession(newLogin, currentUser.password)
                    }
                }
            }
            saveChangeButton.isEnabled = false
        }

        return root
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(p0: Editable?) {
            saveChangeButton.isEnabled = true
        }

    }

    private val itemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            if (p2 != previousSpinnerPosition) {
                saveChangeButton.isEnabled = true
                previousSpinnerPosition = p2
            }
        }

        override fun onNothingSelected(p0: AdapterView<*>?) {
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}