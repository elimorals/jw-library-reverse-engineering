    public final void p(Intent intent, Activity activity) {
        kotlin.jvm.internal.s.f(intent, "intent");
        kotlin.jvm.internal.s.f(activity, "activity");
        try {
            String strO = o(intent);
            if (strO != null) {
                I7.i.d(this.f9097o, null, null, new b(H8.b.f5349a.d((NetworkGate) this.f9094l.invoke(), (LockedGateHandlerFactory) this.f9095m.invoke()), this, strO, null), 3, null);
            } else {
                if (!F(intent, activity)) {
                    D(intent, activity);
                }
            }
        } finally {
            intent.setData(null);
        }
    }
