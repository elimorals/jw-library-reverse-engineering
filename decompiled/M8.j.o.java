    private final String o(Intent intent) {
        Uri data = intent.getData();
        if (data != null && kotlin.jvm.internal.s.b(data.getScheme(), "jwlibrary") && kotlin.jvm.internal.s.b(data.getHost(), "jwl-beta-dropbox")) {
            return data.getQueryParameter("token");
        }
        return null;
    }
