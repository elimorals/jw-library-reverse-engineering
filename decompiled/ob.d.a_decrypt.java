    public a a(u[] token) {
        s.f(token, "token");
        byte bJ = token[0].j();
        int iC = u.c((byte) (bJ & 15)) & 255;
        int i10 = bJ & 255;
        int i11 = (i10 >> 4) & 1;
        int i12 = (i10 >> 5) & 7;
        return new a((i12 > 0 || i11 > 0) ? 1 : 0, i12, i11, iC, (u[]) l.p(token, 1, token.length));
    }
