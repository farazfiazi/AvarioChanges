package io.avario.fansolution.android.launch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.avario.fansolution.android.DaggerPresenterComponent
import io.avario.fansolution.android.PresenterModule
import io.avario.fansolution.android.common.dagger.GraphComponentAccessor
import io.avario.fansolution.android.onboarding.OnboardingActivity
import io.avario.fansolution.android.webview.WebViewActivity
import javax.inject.Inject

class LaunchActivity : AppCompatActivity(), LaunchView {

    @Inject
    lateinit var presenter: LaunchPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DaggerPresenterComponent
            .builder()
            .appComponent((application as GraphComponentAccessor).appComponent)
            .presenterModule(PresenterModule(this))
            .build()
            .inject(this)

        presenter.onViewReady()
    }

    override fun displayWebview() {
        presenter.setSessionExpireMillis(0)
        startActivity(WebViewActivity.newInstance(this))
        finish()
    }

    override fun displayOnBoarding(sessionConnected: Boolean) {
        val intent = OnboardingActivity.newInstance(this)
        intent.putExtra(OnboardingActivity.SESSION_CONNECTED, sessionConnected)

        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        presenter.onFinish()
        super.onDestroy()
    }
}
