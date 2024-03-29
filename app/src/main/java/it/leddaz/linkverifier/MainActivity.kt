package it.leddaz.linkverifier

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.color.DynamicColors
import com.google.android.material.elevation.SurfaceColors
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import java.util.TreeMap


/**
 * The app's main activity, started at launch.
 * @return The activity
 * @author Leonardo Ledda (LeddaZ)
 */
class MainActivity : AppCompatActivity() {

    /* Map to store the package name and corresponding app name,
     * alphabetically ordered by app name.
     */
    private lateinit var appNames: TreeMap<String, String>

    /**
     * Actions executed when the activity is created at runtime.
     * @property savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DynamicColors.applyToActivityIfAvailable(this)
        window.navigationBarColor = SurfaceColors.SURFACE_0.getColor(this)
        window.statusBarColor = SurfaceColors.SURFACE_0.getColor(this)
        setContentView(R.layout.activity_main)
        val systemAppsSwitch = findViewById<MaterialSwitch>(R.id.systemAppsSwitch)
        populateAppList(systemAppsSwitch.isChecked)
        systemAppsSwitch.setOnCheckedChangeListener{ _, isChecked ->
            populateAppList(isChecked)
        }
    }

    /**
     * Required to regenerate the app list on configuration changes
     * (dark theme, orientation, etc). It's ugly but it works.
     */
    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
    }

    /**
     * Populates the list of apps installed by the user.
     */
    private fun populateAppList(isChecked: Boolean) {
        val pm = packageManager
        appNames = TreeMap()
        val flags = PackageManager.GET_META_DATA
        val packages = pm.getInstalledPackages(flags)
        for (item in packages) {
            val applicationInfo: ApplicationInfo = item.applicationInfo
            if(isChecked) {
                val name = pm.getApplicationLabel(applicationInfo)
                val packageName = applicationInfo.packageName
                appNames[name.toString()] = packageName.toString()
            } else {
                if (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 1) {
                    val name = pm.getApplicationLabel(applicationInfo)
                    val packageName = applicationInfo.packageName
                    appNames[name.toString()] = packageName.toString()
                }
            }
        }
        val appNamesArray: MutableSet<String> = appNames.keys
        val textField = findViewById<AutoCompleteTextView>(R.id.selectedApp)
        val materialTextField = textField as MaterialAutoCompleteTextView
        materialTextField.setSimpleItems(appNamesArray.toTypedArray())
    }

    /**
     * Runs when the Open button is pressed; opens the verified links settings
     * for the chosen app.
     * @property view the view which contains the button
     */
    @Suppress("UNUSED_PARAMETER")
    fun openLinkSettings(view: View) {
        val textField = findViewById<AutoCompleteTextView>(R.id.selectedApp)
        val app = textField.text.toString()
        var packageName = ""
        for (entry in appNames.entries.iterator()) {
            if (app == entry.key)
                packageName = entry.value
        }
        intent = Intent(
            Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS,
            Uri.parse("package:$packageName")
        )
        this.startActivity(intent)
    }

    /**
     * Runs when the See on GitHub button is pressed; opens the app's page
     * on GitHub.
     * @property view the view which contains the button
     */
    @Suppress("UNUSED_PARAMETER")
    fun openGitHub(view: View) {
        val browserIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/LeddaZ/LinkVerifier"))
        startActivity(browserIntent, null)
    }
}
