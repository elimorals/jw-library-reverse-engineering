    private final boolean D(Intent intent, final Context context) {
        final Uri data;
        if (!kotlin.jvm.internal.s.b("android.intent.action.VIEW", intent.getAction()) || (data = intent.getData()) == null) {
            return false;
        }
        ((Dispatcher) J8.c.a().a(Dispatcher.class)).b(new Runnable() { // from class: M8.c
            @Override // java.lang.Runnable
            public final void run() {
                j.E(this.f9055o, context, data);
            }
        });
        return true;
    }
