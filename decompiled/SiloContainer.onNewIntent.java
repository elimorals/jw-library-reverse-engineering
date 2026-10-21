    @Override // androidx.activity.ComponentActivity, android.app.Activity
    protected void onNewIntent(Intent intent) {
        kotlin.jvm.internal.s.f(intent, "intent");
        super.onNewIntent(intent);
        boolean zB = kotlin.jvm.internal.s.b("android.intent.action.SEARCH", intent.getAction());
        this.f39683f0 = zB;
        if (zB) {
            D2(intent);
        } else {
            ((M8.j) J8.c.a().a(M8.j.class)).p(intent, this);
        }
    }
