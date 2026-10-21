/* JADX INFO: loaded from: classes2.dex */
public class b {
    /* JADX WARN: Multi-variable type inference failed */
    public u[] a(a configParams, u[] salt, w wVar) {
        s.f(configParams, "configParams");
        s.f(salt, "salt");
        g gVar = null;
        Object[] objArr = 0;
        if (configParams.b() == 0) {
            return null;
        }
        List listB0 = l.B0(configParams.a());
        int i10 = 1;
        if (configParams.e() == 1 && wVar != null) {
            u[] uVarArr = new u[4];
            for (int i11 = 0; i11 < 4; i11++) {
                uVarArr[i11] = u.a((byte) 0);
            }
            uVarArr[3] = u.a(u.c((byte) (wVar.j() & SonyType1MakernoteDirectory.TAG_NO_PRINT)));
            uVarArr[2] = u.a(u.c((byte) ((wVar.j() >>> 8) & SonyType1MakernoteDirectory.TAG_NO_PRINT)));
            uVarArr[1] = u.a(u.c((byte) ((wVar.j() >>> 16) & SonyType1MakernoteDirectory.TAG_NO_PRINT)));
            uVarArr[0] = u.a(u.c((byte) ((wVar.j() >>> 24) & SonyType1MakernoteDirectory.TAG_NO_PRINT)));
            listB0.addAll(l.y0(uVarArr));
        }
        int iB = (configParams.b() * 10) + 5;
        P5.a aVar = new P5.a(gVar, i10, objArr == true ? 1 : 0);
        P5.d dVar = P5.d.ARGON2_ID;
        byte[] bArrA = c0.a(listB0);
        byte[] bArrCopyOf = Arrays.copyOf(bArrA, bArrA.length);
        s.e(bArrCopyOf, "java.util.Arrays.copyOf(this, size)");
        byte[] bArrB = m7.a.b(salt);
        byte[] bArrCopyOf2 = Arrays.copyOf(bArrB, bArrB.length);
        s.e(bArrCopyOf2, "java.util.Arrays.copyOf(this, size)");
        byte[] bArrA2 = aVar.b(dVar, bArrCopyOf, bArrCopyOf2, iB, SQLiteDatabase.OPEN_SHAREDCACHE, 2, 32, e.V13).a();
        byte[] bArrCopyOf3 = Arrays.copyOf(bArrA2, bArrA2.length);
        s.e(bArrCopyOf3, "java.util.Arrays.copyOf(this, size)");
        return m7.a.a(v.f(bArrCopyOf3));
    }
