package com.example.viewpagerexperiment.ui.autorization

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import com.example.viewpagerexperiment.R


class AuthorizationByPhoneOfAnIndividualFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(
            R.layout.fragment_authorization_by_phone_of_an_individual,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)






        textChangeListener(view)


    }

    private fun textChangeListener(view: View) {
        val edTelData=view.findViewById<EditText>(R.id.phoneNumber_edit)




        edTelData.addTextChangedListener(object : PhoneNumberFormattingTextWatcher() {
            //we need to know if the user is erasing or inputing some new character
            private var backspacingFlag = false

            //we need to block the :afterTextChanges method to be called again after we just replaced the EditText text
            private var editedFlag = false

            //we need to mark the cursor position and restore it after the edition
            private var cursorComplement = 0
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                //we store the cursor local relative to the end of the string in the EditText before the edition
                cursorComplement = s.length - edTelData.selectionStart
                //we check if the user ir inputing or erasing a character
                backspacingFlag = count > after
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // nothing to do here =D
            }

            override fun afterTextChanged(s: Editable) {
                val string = s.toString()
                //what matters are the phone digits beneath the mask, so we always work with a raw string with only digits
                val phone = string.replace("[^\\d]".toRegex(), "")

                var i='1'
               try {
                   i = if (s[0] == '+'){

                       s[1]

                   }else{
                       s[0]
                   }
               }catch (e:Exception){
                   Toast.makeText(requireContext(),e.message.toString(),Toast.LENGTH_LONG).show()

               }




                //if the text was just edited, :afterTextChanged is called another time... so we need to verify the flag of edition
                //if the flag is false, this is a original user-typed entry. so we go on and do some magic
                if (!editedFlag) {

                    //we start verifying the worst case, many characters mask need to be added
                    //example: 999999999 <- 6+ digits already typed
                    // masked: (999) 999-999
                    if (phone.length >= 9 && !backspacingFlag) {
                        //we will edit. next call on this textWatcher will be ignored
                        editedFlag = true
                        //here is the core. we substring the raw digits and add the mask as convenient
                        val ans = "+${i}(" + phone.substring(1, 4) + ") " + phone.substring(
                            4,
                            7
                        ) + "-" + phone.substring(7, 9) + "-" + phone.substring(9)
                        edTelData.setText(ans)
                        //we deliver the cursor to its original position relative to the end of the string
                        edTelData.setSelection(edTelData.getText().length - cursorComplement)

                        //we end at the most simple case, when just one character mask is needed
                        //example: 99999 <- 3+ digits already typed
                        // masked: (999) 99
                    } else if (phone.length >= 4 && !backspacingFlag) {
                        editedFlag = true
                        val ans = "+${i}(" + phone.substring(1, 4) + ") " + phone.substring(4)
                        edTelData.setText(ans)
                        edTelData.setSelection(edTelData.getText().length - cursorComplement)
                    }
                    // We just edited the field, ignoring this cicle of the watcher and getting ready for the next
                } else {
                    editedFlag = false
                }
            }
        })
    }

}