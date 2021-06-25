package hamidov.sardor.stsa

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class Myapp : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(applicationContext)
        val config = RealmConfiguration.Builder().name("MyHospital.realm").build()
        Realm.setDefaultConfiguration(config)
    }
}