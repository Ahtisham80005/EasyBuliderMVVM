package com.tradesk.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.tradesk.R
import com.tradesk.activity.auth.ChangePasswordActivity
import com.tradesk.activity.auth.LoginActivity
import com.tradesk.databinding.ActivitySettingsBinding
import com.tradesk.preferences.PreferenceHelper
import com.tradesk.util.extension.AllinOneDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {
    lateinit var binding:ActivitySettingsBinding
    @Inject
    lateinit var mPrefs: PreferenceHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_settings)
        binding.mIvBack.setOnClickListener { finish() }
        binding.clChangePassword.setOnClickListener { startActivity(Intent(this,
            ChangePasswordActivity::class.java).putExtra("type","Change Password")) }
        binding.clTerms.setOnClickListener { startActivity(Intent(this,ChangePasswordActivity::class.java).putExtra("type","Terms $ Conditions")) }
        binding.clPrivacy.setOnClickListener { startActivity(Intent(this,ChangePasswordActivity::class.java).putExtra("type","Privacy Policy")) }
        binding.clLogout.setOnClickListener {
            AllinOneDialog(ttle = "Log out",
                msg = "Are you sure you want to Log Out ?",
                onLeftClick = {/*btn No click*/ },
                onRightClick = {/*btn Yes click*/
                    Toast.makeText(
                        this,
                        "Your are successfully logged out.",
                        Toast.LENGTH_SHORT
                    ).show()
                    mPrefs.logoutUser()

                    startActivity(
                        Intent(this, LoginActivity::class.java).putExtra("logout", "true"))
                    finishAffinity()
//                    startAnim()
                }
            ) }
    }
}