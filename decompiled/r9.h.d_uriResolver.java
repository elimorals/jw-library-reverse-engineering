    /* JADX WARN: Code duplicated, block: B:13:0x0075  */
    /* JADX WARN: Code duplicated, block: B:15:0x0082  */
    public final void d(Context context, Uri uri) {
        ListenableFuture finderNavigateFuture;
        s.f(context, "context");
        s.f(uri, "uri");
        mb.b bVarG = new k().g(uri);
        if (bVarG == null) {
            finderNavigateFuture = p.d(Boolean.FALSE);
        } else {
            if (s.b(uri.getScheme(), "jwlibrary")) {
                finderNavigateFuture = this.f47659d.a(context, uri, bVarG, new b());
            } else {
                LanguagesInfo languagesInfoF = i.g().J().f();
                Language languageC = languagesInfoF.c(languagesInfoF.a(bVarG.f()));
                if (languageC == null || !languageC.l()) {
                    finderNavigateFuture = this.f47659d.a(context, uri, bVarG, new b());
                } else {
                    Intent intent = new Intent("android.intent.action.VIEW", uri.buildUpon().scheme("jwlibrarysl").build());
                    List<ResolveInfo> listQueryIntentActivities = context.getPackageManager().queryIntentActivities(intent, 0);
                    s.e(listQueryIntentActivities, "context.packageManager.q…ntActivities(slIntent, 0)");
                    if (k7.s.X(listQueryIntentActivities)) {
                        context.startActivity(intent);
                        finderNavigateFuture = p.d(Boolean.TRUE);
                    } else {
                        finderNavigateFuture = this.f47659d.a(context, uri, bVarG, new b());
                    }
                }
            }
            if (finderNavigateFuture == null) {
                finderNavigateFuture = p.d(Boolean.FALSE);
            }
        }
        s.e(finderNavigateFuture, "finderNavigateFuture");
        a aVar = new a(uri, this, context, bVarG);
        v vVarF = i.g().F();
        s.e(vVarF, "get().executorService");
        C8.b.a(finderNavigateFuture, aVar, vVarF);
    }
