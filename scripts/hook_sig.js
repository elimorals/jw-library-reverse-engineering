'use strict';
// Hook de la verificación de firma Ed25519 y la derivación/descifrado de JW Library
function hex(b){ try{ if(!b) return 'null'; var r=''; var n=Math.min(b.length,32);
  for(var i=0;i<n;i++){ var x=(b[i]&0xff).toString(16); r+=(x.length<2?'0':'')+x; }
  return r+(b.length>32?'…('+b.length+'B)':' ('+b.length+'B)'); }catch(e){return '?';} }

Java.perform(function(){
  console.log('[*] hooks cargando');

  // 1) Primitiva Ed25519 (JNI SodiumHelper)
  try{
    var S = Java.use('org.watchtower.dss.SodiumHelper');
    ['cryptoSignVerifyDetached','cryptoSignFinalVerify','cryptoSignInit','cryptoSignUpdate'].forEach(function(m){
      if(S[m]){
        S[m].overloads.forEach(function(ov){
          ov.implementation = function(){
            var r = ov.apply(this, arguments);
            console.log('[SIG] SodiumHelper.'+m+' args='+arguments.length+' -> '+r);
            return r;
          };
        });
      }
    });
    console.log('[+] SodiumHelper hooked');
  }catch(e){ console.log('[-] SodiumHelper: '+e); }

  // 2) Verificador de bloque (pb.c.b)
  try{
    var C = Java.use('pb.c');
    C.b.overloads.forEach(function(ov){
      ov.implementation = function(){
        var r = ov.apply(this, arguments);
        console.log('[VERIFY] pb.c.b(...) -> '+r);
        return r;
      };
    });
    console.log('[+] pb.c.b hooked');
  }catch(e){ console.log('[-] pb.c: '+e); }

  // 3) Argon2id KDF (P5.a.b)
  try{
    var P = Java.use('P5.a');
    P.b.overloads.forEach(function(ov){
      ov.implementation = function(){
        var r = ov.apply(this, arguments);
        console.log('[KDF] Argon2 P5.a.b() derivó clave');
        return r;
      };
    });
    console.log('[+] P5.a.b (Argon2) hooked');
  }catch(e){ console.log('[-] P5.a: '+e); }

  // 4) Verificación de assets de alto nivel (z9.a.b con la trustedRootKey)
  try{
    var Z = Java.use('z9.a');
    Z.b.overloads.forEach(function(ov){
      ov.implementation = function(){
        var r = ov.apply(this, arguments);
        console.log('[ASSET-VERIFY] z9.a.b(stream, name) -> result='+r);
        return r;
      };
    });
    console.log('[+] z9.a.b hooked');
  }catch(e){ console.log('[-] z9.a: '+e); }

  console.log('[*] todos los hooks instalados; esperando actividad...');
});
