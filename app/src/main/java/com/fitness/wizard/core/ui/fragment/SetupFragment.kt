package com.fitness.wizard.core.ui.fragment

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.navigation.fragment.findNavController
import com.fitness.wizard.R
import com.fitness.wizard.core.util.Constants.KEY_FIRST_TIME_TOGGLE
import com.fitness.wizard.core.util.Constants.KEY_NAME
import com.fitness.wizard.core.util.Constants.KEY_WEIGHT
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment : Fragment(R.layout.fragment_setup) {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @set: Inject
    var isFirstTime:Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val saveBtn = view.findViewById<Button>(R.id.saveContinueButton)

        if (!isFirstTime){
            findNavController().navigate(R.id.action_setupFragment_to_mainFragment)
        }

        saveBtn.setOnClickListener{
            if(writePersonalDataToSharedPreference()){
                findNavController().navigate(R.id.action_setupFragment_to_mainFragment)
            } else {
                Toast.makeText(requireContext(), "Name or Weight is Empty", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun writePersonalDataToSharedPreference() : Boolean{

        val inpName = view?.findViewById<TextInputLayout>(R.id.inpName)
        val inpWeight = view?.findViewById<TextInputLayout>(R.id.inpWeight)

        val name = inpName?.editText?.text.toString()
        val weight = inpWeight?.editText?.text.toString()

        if (name.isBlank() || weight.isBlank() || !weight.isDigitsOnly()){
            return false
        }
        sharedPreferences.edit()
            .putString(KEY_NAME, name)
            .putFloat(KEY_WEIGHT, weight.toFloat())
            .putBoolean(KEY_FIRST_TIME_TOGGLE, false)
            .apply()

        return true
    }

}