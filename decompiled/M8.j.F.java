    /* JADX WARN: Code duplicated, block: B:86:0x01b3  */
    private final boolean F(Intent intent, Activity activity) {
        boolean z10;
        String scheme;
        Cursor cursorQuery;
        if (kotlin.jvm.internal.s.b("android.intent.action.SEND", intent.getAction()) || kotlin.jvm.internal.s.b("android.intent.action.SEND_MULTIPLE", intent.getAction())) {
            Collection collectionA = Y9.a.f17125a.a(intent);
            if (collectionA.isEmpty()) {
                z10 = true;
            } else {
                Collection collection = collectionA;
                String path = ((Uri) k7.s.f0(collection)).getPath();
                if (path == null) {
                    z10 = true;
                } else {
                    if (G7.m.v(path, ".jwpub", false, 2, null)) {
                        B((Uri) k7.s.f0(collection), "jwpub", activity);
                        return true;
                    }
                    z10 = true;
                    if (G7.m.v(path, ".jwlibrary", false, 2, null)) {
                        B((Uri) k7.s.f0(collection), "jwlibrary", activity);
                        return true;
                    }
                }
            }
            Intent intent2 = new Intent(activity, (Class<?>) ImportDialogActivity.class);
            intent2.putExtra("android.intent.extra.INTENT", intent);
            d1.a.m(activity, intent2, null);
            return z10;
        }
        Uri data = intent.getData();
        if (data == null || (((scheme = data.getScheme()) != null && G7.m.I(scheme, "http", false, 2, null)) || kotlin.jvm.internal.s.b(data.getScheme(), "jwlibrary"))) {
            return false;
        }
        try {
            cursorQuery = activity.getContentResolver().query(data, new String[]{"_display_name"}, null, null, null);
        } catch (SecurityException unused) {
            cursorQuery = null;
        }
        if (cursorQuery != null) {
            if (cursorQuery.moveToFirst()) {
                String string = cursorQuery.getString(0);
                if (string == null) {
                    cursorQuery.close();
                    B(data, "jwpub", activity);
                    return true;
                }
                String[] strArr = (String[]) new G7.j("\\.").h(string, 0).toArray(new String[0]);
                if (!(strArr.length == 0)) {
                    String str = strArr[strArr.length - 1];
                    if (G7.m.w(str, "jwpub", true) || G7.m.w(str, "jwlibrary", true) || G7.m.w(str, "jwlplaylist", true)) {
                        intent.setData(null);
                        B(data, str, activity);
                    } else if (G7.m.w("mp4", str, true) || G7.m.w("m4v", str, true) || G7.m.w("mp3", str, true)) {
                        Intent intent3 = new Intent(activity, (Class<?>) ImportDialogActivity.class);
                        intent3.putExtra("android.intent.extra.INTENT", intent);
                        d1.a.m(activity, intent3, null);
                    } else {
                        v(activity);
                    }
                }
            } else {
                B(data, "jwpub", activity);
            }
            cursorQuery.close();
            return true;
        }
        String string2 = data.toString();
        kotlin.jvm.internal.s.e(string2, "data.toString()");
        Locale ROOT = Locale.ROOT;
        kotlin.jvm.internal.s.e(ROOT, "ROOT");
        String lowerCase = string2.toLowerCase(ROOT);
        kotlin.jvm.internal.s.e(lowerCase, "toLowerCase(...)");
        if (G7.m.N(lowerCase, "?", false, 2, null)) {
            lowerCase = lowerCase.substring(0, G7.m.Z(lowerCase, '?', 0, false, 6, null));
            kotlin.jvm.internal.s.e(lowerCase, "substring(...)");
        }
        if (G7.m.v(lowerCase, ".jwpub", false, 2, null)) {
            B(data, "jwpub", activity);
        } else if (G7.m.v(lowerCase, ".jwlibrary", false, 2, null)) {
            B(data, "jwlibrary", activity);
        } else {
            List listQ = k7.s.q(".mp4", ".m4v", ".mp3");
            if (listQ == null || !listQ.isEmpty()) {
                Iterator it = listQ.iterator();
                while (it.hasNext()) {
                    if (G7.m.v(lowerCase, (String) it.next(), false, 2, null)) {
                        Intent intent4 = new Intent(activity, (Class<?>) ImportDialogActivity.class);
                        intent4.putExtra("android.intent.extra.INTENT", intent);
                        d1.a.m(activity, intent4, null);
                    }
                }
                v(activity);
            } else {
                v(activity);
            }
        }
        return true;
    }
