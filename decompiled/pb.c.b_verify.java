status: available fell_back: False
    /* JADX INFO: Access modifiers changed from: private */
    public final boolean b(f fVar, h hVar, byte[] bArr) {
        fVar.b(this.f46939b);
        byte[] bArrE = hVar.e();
        byte[] bArrS0 = l.s0(bArrE, D7.m.s(0, bArrE.length - 64));
        byte[] bArr2 = new byte[64];
        for (int i10 = 0; i10 < 64; i10++) {
            bArr2[i10] = 0;
        }
        fVar.b(l.w(bArrS0, bArr2));
        fVar.b(this.f46940c);
        return fVar.a(bArr, hVar.g());
    }
