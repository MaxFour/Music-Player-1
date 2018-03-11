packagecom.android.music.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;

importcom.android.music.MusicApplication;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * This is a separate testing class than {@link MPlayerUtilsTest} as PowerMock and Robolectric
 * can't work together until Robolectric 3.3 is released:
 * https://github.com/robolectric/robolectric/wiki/Using-PowerMock
 * <p>
 * Use the devDebug build variant to run.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({PreferenceManager.class, MusicApplication.class, MPlayerUtils.class})
public class MPlayerUtilsPowerMockTest {

    @Test
    public void testIsOnlineWifiConnected() {
        MusicApplication mockApplication = mock(MusicApplication.class);
        SharedPreferences mockSharedPreferences = mock(SharedPreferences.class);
        ConnectivityManager mockConnectivityManager = mock(ConnectivityManager.class);
        NetworkInfo mockNetworkInfo = mock(NetworkInfo.class);

        mockStatic(PreferenceManager.class);
        mockStatic(MusicApplication.class);

        when(PreferenceManager.getDefaultSharedPreferences(any(Context.class))).thenReturn(mockSharedPreferences);
        when(MusicApplication.getInstance()).thenReturn(mockApplication);

        // Mock the connection to Wi-Fi
        when(mockApplication.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockConnectivityManager);
        when(mockConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)).thenReturn(mockNetworkInfo);
        when(mockNetworkInfo.isConnectedOrConnecting()).thenReturn(true);

        // Test when we care about Wi-Fi (and it's connected), regardless of user preference
        assertThat(MPlayerUtils.isOnline(true)).isTrue();

        // Test when we don't care about Wi-Fi (but it's connected anyway), regardless of user preference
        assertThat(MPlayerUtils.isOnline(false)).isTrue();
    }

    @Test
    public void testIsOnlineCellularConnected() {
        MusicApplication mockApplication = mock(MusicApplication.class);
        SharedPreferences mockSharedPreferences = mock(SharedPreferences.class);
        ConnectivityManager mockConnectivityManager = mock(ConnectivityManager.class);
        NetworkInfo mockNetworkInfo = mock(NetworkInfo.class);

        mockStatic(PreferenceManager.class);
        mockStatic(MusicApplication.class);

        when(PreferenceManager.getDefaultSharedPreferences(any(Context.class))).thenReturn(mockSharedPreferences);
        when(MusicApplication.getInstance()).thenReturn(mockApplication);

        // Mock the connection to cellular data, but not Wi-Fi
        when(mockApplication.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockConnectivityManager);
        when(mockConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)).thenReturn(null);
        when(mockConnectivityManager.getActiveNetworkInfo()).thenReturn(mockNetworkInfo);
        when(mockNetworkInfo.isConnectedOrConnecting()).thenReturn(true);

        // Test when we care about Wi-Fi and so does the user (but only cellular is connected)
        when(mockSharedPreferences.getBoolean(eq("pref_download_wifi_only"), anyBoolean())).thenReturn(true);
        assertThat(MPlayerUtils.isOnline(true)).isFalse();

        // Test when we care about Wi-Fi, but the user doesn't (and only cellular is connected)
        when(mockSharedPreferences.getBoolean(eq("pref_download_wifi_only"), anyBoolean())).thenReturn(false);
        assertThat(MPlayerUtils.isOnline(true)).isTrue();

        // Test when we don't care about Wi-Fi, even if the user does (and only cellular is connected)
        when(mockSharedPreferences.getBoolean(eq("pref_download_wifi_only"), anyBoolean())).thenReturn(true);
        assertThat(MPlayerUtils.isOnline(false)).isTrue();

        // Test when we don't care about Wi-Fi and neither does the user (and only cellular is connected)
        when(mockSharedPreferences.getBoolean(eq("pref_download_wifi_only"), anyBoolean())).thenReturn(false);
        assertThat(MPlayerUtils.isOnline(false)).isTrue();
    }

    @Test
    public void testIsUpgraded() throws Exception {
        MusicApplication mockApplication = mock(MusicApplication.class);
        SharedPreferences mockSharedPreferences = mock(SharedPreferences.class);

        mockStatic(PreferenceManager.class);
        mockStatic(MusicApplication.class);

        when(PreferenceManager.getDefaultSharedPreferences(any(Context.class))).thenReturn(mockSharedPreferences);
        when(MusicApplication.getInstance()).thenReturn(mockApplication);

        // If our Application class is upgraded, then the user is upgraded
        when(mockApplication.getIsUpgraded()).thenReturn(true);
        assertThat(MPlayerUtils.isUpgraded()).isTrue();

        // Set the Application upgraded value back to false
        when(mockApplication.getIsUpgraded()).thenReturn(false);

        // We're upgraded if we're a legacy user with the upgrade preference
        when(mockSharedPreferences.getBoolean("pref_theme_gold", false)).thenReturn(true);
        assertThat(MPlayerUtils.isUpgraded()).isTrue();

        // Set the 'legacy' upgrade back to false
        when(mockSharedPreferences.getBoolean("pref_theme_gold", false)).thenReturn(false);

        // We're upgraded if the package name is the Music+ package name
        when(mockApplication.getPackageName()).thenReturn("com.android.music");
        assertThat(MPlayerUtils.isUpgraded()).isTrue();

        // We're not upgraded for dodgy package names
        when(mockApplication.getPackageName()).thenReturn("bad.package.name");
        assertThat(MPlayerUtils.isUpgraded()).isFalse();
    }

    @Test
    public void testGetIpAddr() throws Exception {
        MusicApplication mockApplication = mock(MusicApplication.class);
        WifiManager mockWifiManager = mock(WifiManager.class);
        WifiInfo mockWifiInfo = mock(WifiInfo.class);

        // Setup mocked IP Address of 192.168.1.1
        mockStatic(MusicApplication.class);
        when(MusicApplication.getInstance()).thenReturn(mockApplication);
        when(mockApplication.getSystemService(Context.WIFI_SERVICE)).thenReturn(mockWifiManager);
        when(mockWifiManager.getConnectionInfo()).thenReturn(mockWifiInfo);
        when(mockWifiInfo.getIpAddress()).thenReturn(16885952);

        assertThat(MPlayerUtils.getIpAddr()).isEqualTo("192.168.1.1");
    }
}
