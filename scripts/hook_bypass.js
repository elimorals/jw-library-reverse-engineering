'use strict';
// PoC: forzar que la verificación de firma FALLE, para demostrar que es el gate
// que separa contenido aceptado de rechazado. (Lo inverso de un bypass.)
Java.perform(function(){
  console.log('[*] bypass/gate-test cargando');
  // Forzar cryptoSignVerifyDetached y FinalVerify a devolver código de ERROR (!=0)
  try{
    var S = Java.use('org.watchtower.dss.SodiumHelper');
    ['cryptoSignVerifyDetached','cryptoSignFinalVerify'].forEach(function(m){
      S[m].overloads.forEach(function(ov){
        ov.implementation = function(){
          var real = ov.apply(this, arguments);
          var forced = -1;  // forzar fallo
          console.log('[GATE] '+m+' real='+real+' -> FORZADO='+forced);
          return forced;
        };
      });
    });
    console.log('[+] verificación forzada a FALLAR');
  }catch(e){ console.log('[-] '+e); }

  // Observar el enum de resultado de validación (pb.j) y z9.a.b
  try{
    var Z = Java.use('z9.a');
    Z.b.overloads.forEach(function(ov){
      ov.implementation = function(){
        var r = ov.apply(this, arguments);
        console.log('[ASSET-VERIFY] z9.a.b -> '+r+'  (debe ser != Success)');
        return r;
      };
    });
  }catch(e){ console.log('[-] z9 '+e); }
  console.log('[*] listo: con la firma forzada a fallar, el contenido legítimo debe ser RECHAZADO');
});
