% --- HECHOS DE ARTISTAS Y SUS HABILIDADES ---
artista(brian_may, base).
habilidad(brian_may, guitarra_eléctrica).
habilidad(brian_may, voz_secundaria).
artista(roger_taylor, base).
habilidad(roger_taylor, batería).
habilidad(roger_taylor, voz_secundaria).
artista(john_deacon, base).
habilidad(john_deacon, bajo).
artista(george_michael, contratado).
habilidad(george_michael, voz_principal).
artista(elton_john, contratado).
habilidad(elton_john, voz_principal).
habilidad(elton_john, piano).
artista(david_bowie, contratado).
habilidad(david_bowie, voz_principal).
artista(annie_lennox, contratado).
habilidad(annie_lennox, voz_principal).
artista(lisa_stansfield, contratado).
habilidad(lisa_stansfield, voz_principal).
artista(agustin_cruz, contratado).
habilidad(agustin_cruz, voz_principal).
habilidad(agustin_cruz, saxofón).
habilidad(agustin_cruz, armónica).
artista(roberto_musso, contratado).
habilidad(roberto_musso, voz_principal).
habilidad(roberto_musso, saxofón).
habilidad(roberto_musso, armónica).
artista(rita_lee, contratado).
habilidad(rita_lee, voz_principal).
habilidad(rita_lee, saxofón).
habilidad(rita_lee, armónica).
habilidad(rita_lee, acordeón).
artista(lucy_patané, contratado).
habilidad(lucy_patané, voz_principal).
habilidad(lucy_patané, saxofón).
habilidad(lucy_patané, armónica).
habilidad(lucy_patané, acordeón).

% --- REGLAS ESTÁTICAS DE COSTE ---
coste_entrenamiento(A, R, 0) :- habilidad(A, R).
coste_entrenamiento(A, R, 1) :- artista(A, _), \+ habilidad(A, R).
