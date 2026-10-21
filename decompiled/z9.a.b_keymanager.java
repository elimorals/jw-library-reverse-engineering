    @Override // Ma.a
    public j b(InputStream stream, String assetName) {
        j jVarA;
        s.f(stream, "stream");
        s.f(assetName, "assetName");
        synchronized (this.f53130c) {
            try {
                int iAvailable = stream.available();
                e eVarC = new c(this.f53128a.c()).c(new i(stream, y.c(iAvailable), null), this.f53130c);
                if (eVarC.a() != j.Success) {
                    this.f53129b.i(assetName, iAvailable);
                }
                jVarA = eVarC.a();
            } catch (Throwable th) {
                throw th;
            }
        }
        return jVarA;
    }
