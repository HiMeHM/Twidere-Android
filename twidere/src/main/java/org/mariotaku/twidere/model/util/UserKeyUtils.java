package org.mariotaku.twidere.model.util;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.mariotaku.twidere.TwidereConstants;
import org.mariotaku.twidere.api.twitter.model.User;
import org.mariotaku.twidere.model.UserKey;
import org.mariotaku.twidere.provider.TwidereDataStore.Accounts;
import org.mariotaku.twidere.util.DataStoreUtils;
import org.mariotaku.twidere.util.media.preview.PreviewMediaExtractor;

import java.util.ArrayList;

/**
 * Created by mariotaku on 16/3/7.
 */
public class UserKeyUtils {

    @Nullable
    public static UserKey findById(Context context, long id) {
        final String[] projection = {Accounts.ACCOUNT_KEY};
        final Cursor cur = DataStoreUtils.findAccountCursorsById(context, projection, id);
        if (cur == null) return null;
        try {
            if (cur.moveToFirst()) return UserKey.valueOf(cur.getString(0));
        } finally {
            cur.close();
        }
        return null;
    }

    @NonNull
    public static UserKey[] findByIds(Context context, long... id) {
        final String[] projection = {Accounts.ACCOUNT_KEY};
        final Cursor cur = DataStoreUtils.findAccountCursorsById(context, projection, id);
        if (cur == null) return new UserKey[0];
        try {
            final ArrayList<UserKey> accountKeys = new ArrayList<>();
            cur.moveToFirst();
            while (!cur.isAfterLast()) {
                accountKeys.add(UserKey.valueOf(cur.getString(0)));
                cur.moveToNext();
            }
            return accountKeys.toArray(new UserKey[accountKeys.size()]);
        } finally {
            cur.close();
        }
    }

    public static UserKey fromUser(User user) {
        return new UserKey(user.getId(), getUserHost(user));
    }

    public static String getUserHost(User user) {
        return getUserHost(user.getOstatusUri(), TwidereConstants.USER_TYPE_TWITTER_COM);
    }

    @NonNull
    public static String getUserHost(@Nullable String uri, @Nullable String def) {
        if (def == null) {
            def = TwidereConstants.USER_TYPE_TWITTER_COM;
        }
        if (uri == null) return def;
        final String authority = PreviewMediaExtractor.getAuthority(uri);
        if (authority == null) return def;
        return authority.replaceAll("[^\\w\\d\\.]", "-");
    }

    public static boolean isSameHost(UserKey accountKey, UserKey userKey) {
        return isSameHost(accountKey.getHost(), userKey.getHost());
    }

    public static boolean isSameHost(String a, String b) {
        if (TextUtils.isEmpty(a) || TextUtils.isEmpty(b)) return true;
        return TextUtils.equals(a, b);
    }
}