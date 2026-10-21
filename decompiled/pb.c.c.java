    public final e c(i streamReader, String trustedRootKey) {
        s.f(streamReader, "streamReader");
        s.f(trustedRootKey, "trustedRootKey");
        byte[] bArrA = pb.a.f46926a.a(trustedRootKey);
        if (this.f46938a.a(bArrA)) {
            return new e(j.NoAvailableRootPublicKeys);
        }
        f fVar = new f();
        try {
            byte[] bArrB = streamReader.b((int) streamReader.a());
            Integer numC = b.c(bArrB, this.f46940c);
            if (numC == null) {
                return new e(j.SignatureBlockNotFound);
            }
            int iIntValue = numC.intValue();
            if (iIntValue != (l.S(bArrB) - this.f46940c.length) - 1 && l.j0(bArrB) != this.f46941d) {
                return new e(j.SignatureBlockNotFound);
            }
            Integer numC2 = b.c(bArrB, this.f46939b);
            if (numC2 == null) {
                return new e(j.SignatureBlockNotFound);
            }
            int iIntValue2 = numC2.intValue();
            g.d(fVar, l.s0(bArrB, D7.m.s(0, iIntValue2)));
            byte[] bArrS0 = l.s0(bArrB, D7.m.s(iIntValue2 + this.f46939b.length, iIntValue));
            h.a aVar = h.f46952f;
            Pair pairB = aVar.b(bArrS0);
            h hVar = (h) pairB.c();
            return hVar == null ? new e((j) pairB.d()) : new e(aVar.d(hVar, fVar, bArrA, this.f46938a, new a(fVar, hVar)));
        } catch (Error e10) {
            return new e(e10);
        } finally {
            fVar.d();
        }
    }
