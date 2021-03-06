package org.smartregister.tbr.service;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.domain.Response;
import org.smartregister.service.HTTPAgent;
import org.smartregister.tbr.repository.ConfigurableViewsRepository;
import org.smartregister.tbr.sync.ECSyncHelper;

import static org.smartregister.tbr.repository.ConfigurableViewsRepository.IDENTIFIER;
import static org.smartregister.tbr.service.PullConfigurableViewsIntentService.VIEWS_URL;
import static org.smartregister.tbr.util.Constants.CONFIGURATION.LOGIN;
import static org.smartregister.util.Log.logError;

/**
 * Created by samuelgithengi on 10/27/17.
 */
public class PullConfigurableViewsServiceHelper {
    private static final String TAG = PullConfigurableViewsServiceHelper.class.getCanonicalName();

    private boolean databaseCreated;

    private Context applicationContext;
    private ConfigurableViewsRepository configurableViewsRepository;
    private HTTPAgent httpAgent;
    private String baseUrl;
    private ECSyncHelper syncHelper;

    public PullConfigurableViewsServiceHelper(Context applicationContext, ConfigurableViewsRepository configurableViewsRepository,
                                              HTTPAgent httpAgent, String baseUrl, ECSyncHelper syncHelper, boolean databaseCreated) {

        this.applicationContext = applicationContext;
        this.configurableViewsRepository = configurableViewsRepository;
        this.httpAgent = httpAgent;
        this.baseUrl = baseUrl;
        this.syncHelper = syncHelper;
        this.databaseCreated = databaseCreated;
    }


    protected int processIntent() throws Exception {
        JSONArray views = fetchConfigurableViews();
        if (views != null && views.length() > 0) {
            //There is any other previous login
            if (!databaseCreated) {
                saveLoginConfiguration(views);
            } else {
                views = saveLoginConfiguration(views);
                long lastSyncTimeStamp = configurableViewsRepository.saveConfigurableViews(views);
                syncHelper.updateLastViewsSyncTimeStamp(lastSyncTimeStamp);
            }
        }
        return views == null || !databaseCreated ? 0 : views.length();
    }

    private JSONArray saveLoginConfiguration(JSONArray views) throws JSONException {
        for (int i = 0; i < views.length(); i++) {
            JSONObject jsonObject = views.getJSONObject(i);
            String identifier = jsonObject.getString(IDENTIFIER);
            if (identifier.equals(LOGIN)) {
                syncHelper.updateLoginConfigurableViewPreference(jsonObject.toString());
                views.remove(i);
                break;
            }
        }
        return views;
    }

    private JSONArray fetchConfigurableViews() throws JSONException {
        String endString = "/";
        if (baseUrl.endsWith(endString)) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(endString));
        }

        String url = baseUrl + VIEWS_URL + "?serverVersion=" + ECSyncHelper.getInstance(applicationContext).getLastViewsSyncTimeStamp();
        Log.i(TAG, "URL: " + url);
        if (httpAgent == null) {
            logError(url + " http agent is null");
            return null;
        }


        Response resp = httpAgent.fetchWithCredentials(url, "", "");
        if (resp.isFailure()) {
            logError(url + " not returned data");
            return null;
        }
        return new JSONArray((String) resp.payload());
    }
}
