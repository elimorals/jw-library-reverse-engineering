    @Override // androidx.fragment.app.j, androidx.activity.ComponentActivity, c1.h, android.app.Activity
    public void onCreate(Bundle bundle) {
        Unit unit;
        androidx.activity.r.b(this, null, null, 3, null);
        super.onCreate(bundle);
        this.f39672U = bundle != null ? bundle.getBoolean("wasSearchViewFocused") : true;
        g0().a(this);
        I7.i.d(androidx.lifecycle.p.a(this), null, null, new l(null), 3, null);
        C9.a.f1294a.a(this);
        f39660B0 = this;
        org.jw.jwlibrary.mobile.dialog.e.S(this);
        boolean z10 = false;
        overridePendingTransition(0, 0);
        ViewDataBinding viewDataBindingI = androidx.databinding.g.i(this, R.layout.activity_silo_container);
        kotlin.jvm.internal.s.e(viewDataBindingI, "setContentView(\n        …_silo_container\n        )");
        Y8.c cVar = (Y8.c) viewDataBindingI;
        this.f39690m0 = cVar;
        AppBarLayout appBarLayout = cVar.f16758N.f16982N;
        ViewGroup.LayoutParams layoutParams = cVar.f16764T.getLayoutParams();
        kotlin.jvm.internal.s.d(layoutParams, "null cannot be cast to non-null type androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams");
        U2(new org.jw.jwlibrary.mobile.activity.b(appBarLayout, (CoordinatorLayout.f) layoutParams));
        cVar.w1(V());
        Ya.i.f17144a.v(this.f39699v0);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        b9.a aVar = new b9.a();
        if (Build.VERSION.SDK_INT >= 33) {
            registerReceiver(aVar, intentFilter, 2);
        } else {
            registerReceiver(aVar, intentFilter);
        }
        eb.a.a(new n());
        Object systemService = getSystemService(V8ValueBuiltInSymbol.PROPERTY_SEARCH);
        kotlin.jvm.internal.s.d(systemService, "null cannot be cast to non-null type android.app.SearchManager");
        this.f39693p0 = new T8.h(this, (SearchManager) systemService);
        A1((Toolbar) findViewById(R.id.toolbar));
        this.f39680c0 = q1();
        Object objA = J8.c.a().a(B9.g.class);
        kotlin.jvm.internal.s.e(objA, "get().getInstance(BackupTransporter::class.java)");
        ((B9.g) objA).E().a(new EventHandler() { // from class: M8.A0
            @Override // org.jw.jwlibrary.core.EventHandler
            public final void handle(Object obj, Object obj2) {
                SiloContainer.J2(this.f8972a, obj, obj2);
            }
        });
        this.f39671T = ((PublicationDownloader) J8.c.a().a(PublicationDownloader.class)).b().q(s.f39784o).L(new t());
        this.f39670S = ((MediaDownloader) J8.c.a().a(MediaDownloader.class)).d().q(u.f39789o).L(new v());
        W.n();
        this.f39681d0 = cVar.f16761Q;
        BottomNavigationView bottomNavigationView = cVar.f16759O;
        this.f39696s0 = bottomNavigationView;
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(this);
            Z.B0(bottomNavigationView, new androidx.core.view.G() { // from class: M8.B0
                @Override // androidx.core.view.G
                public final androidx.core.view.A0 a(View view, androidx.core.view.A0 a10) {
                    return SiloContainer.L2(this.f8974a, view, a10);
                }
            });
        }
        C9.g.k(this);
        f0 f0Var = (f0) J8.c.a().a(f0.class);
        if (f0Var.h() == 0) {
            f0Var.l(1);
        }
        C9.D d10 = C9.D.f1241a;
        if (!d10.z(this)) {
            X8.j.k(getApplicationContext());
            d10.Q(this, true);
        }
        if (!C9.k.f1336e) {
            C9.k.f1336e = true;
            H2();
        }
        c cVar2 = f39664F0;
        if (cVar2 != null) {
            cVar2.a(this);
            f39664F0 = null;
            unit = Unit.f36837a;
        } else {
            unit = null;
        }
        if (unit == null) {
            ((org.jw.jwlibrary.mobile.activity.e) this.f39674W.invoke()).h(r9.b.HOME, null, true);
        }
        Q2();
        Intent intent = getIntent();
        boolean z11 = bundle != null && bundle.getBoolean("search already handled");
        this.f39684g0 = bundle != null && bundle.getBoolean("showing_tips");
        this.f39685h0 = bundle != null && bundle.getBoolean("showing_terms_of_use");
        this.f39686i0 = bundle != null && bundle.getBoolean("showing_privacy_acceptance");
        this.f39687j0 = bundle != null && bundle.getBoolean("showing_pronunciation_guide");
        if (bundle != null && bundle.getBoolean("system_update_needed_dialog_dismissed")) {
            z10 = true;
        }
        this.f39688k0 = z10;
        boolean zB = kotlin.jvm.internal.s.b("android.intent.action.SEARCH", intent.getAction());
        this.f39683f0 = zB;
        if (!zB || (z11 && !(this.f39679b0 instanceof k3))) {
            h3();
        } else {
            kotlin.jvm.internal.s.e(intent, "intent");
            D2(intent);
        }
        M8.j jVar = (M8.j) J8.c.a().a(M8.j.class);
        kotlin.jvm.internal.s.e(intent, "intent");
        jVar.p(intent, this);
        j3();
        if (!f39661C0) {
            f39661C0 = true;
            ((Qa.e) J8.c.a().a(Qa.e.class)).d().r(m.f39774a).S(1L).P(X6.a.d()).K();
        }
        BottomNavigationView bottomNavigationView2 = this.f39696s0;
        if (bottomNavigationView2 != null) {
            if (!bottomNavigationView2.isLaidOut() || bottomNavigationView2.isLayoutRequested()) {
                bottomNavigationView2.addOnLayoutChangeListener(new k());
            } else {
                C2(this, null, 1, null);
            }
        }
    }
