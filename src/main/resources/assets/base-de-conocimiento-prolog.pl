% --- GENERADO AUTOMATICAMENTE DESDE JAVA (MAVEN) ---

% --- HECHOS DE ARTISTAS Y HABILIDADES ---
artista(agustin_cruz, contratado).
artista(annie_lennox, contratado).
artista(brian_may, base).
artista(david_bowie, contratado).
artista(elton_john, contratado).
artista(george_michael, contratado).
artista(john_deacon, base).
artista(lisa_stansfield, contratado).
artista(lucy_patane, contratado).
artista(rita_lee, contratado).
artista(roberto_musso, contratado).
artista(roger_taylor, base).
costo_base(agustin_cruz, 500).
costo_base(annie_lennox, 900).
costo_base(brian_may, 0).
costo_base(david_bowie, 1500).
costo_base(elton_john, 1200).
costo_base(george_michael, 1000).
costo_base(john_deacon, 0).
costo_base(lisa_stansfield, 800).
costo_base(lucy_patane, 800).
costo_base(rita_lee, 800).
costo_base(roberto_musso, 300).
costo_base(roger_taylor, 0).
habilidad(agustin_cruz, armonica).
habilidad(agustin_cruz, saxofon).
habilidad(agustin_cruz, voz_principal).
habilidad(annie_lennox, voz_principal).
habilidad(brian_may, guitarra_electrica).
habilidad(brian_may, voz_secundaria).
habilidad(david_bowie, voz_principal).
habilidad(elton_john, piano).
habilidad(elton_john, voz_principal).
habilidad(george_michael, voz_principal).
habilidad(john_deacon, bajo).
habilidad(lisa_stansfield, voz_principal).
habilidad(lucy_patane, acordeon).
habilidad(lucy_patane, armonica).
habilidad(lucy_patane, saxofon).
habilidad(lucy_patane, voz_principal).
habilidad(rita_lee, acordeon).
habilidad(rita_lee, armonica).
habilidad(rita_lee, saxofon).
habilidad(rita_lee, voz_principal).
habilidad(roberto_musso, armonica).
habilidad(roberto_musso, saxofon).
habilidad(roberto_musso, voz_principal).
habilidad(roger_taylor, bateria).
habilidad(roger_taylor, voz_secundaria).
max_canciones(agustin_cruz, 2).
max_canciones(annie_lennox, 2).
max_canciones(brian_may, 100).
max_canciones(david_bowie, 2).
max_canciones(elton_john, 2).
max_canciones(george_michael, 3).
max_canciones(john_deacon, 100).
max_canciones(lisa_stansfield, 2).
max_canciones(lucy_patane, 2).
max_canciones(rita_lee, 2).
max_canciones(roberto_musso, 2).
max_canciones(roger_taylor, 100).

% --- MIEMBROS DE DISCOGRAFICA ---
miembro_discografica(brian_may).
miembro_discografica(john_deacon).
miembro_discografica(lucy_patane).
miembro_discografica(roger_taylor).

% --- HECHOS DE ROLES REQUERIDOS ---
rol_instancia(i1, voz_principal).
rol_instancia(i10, voz_principal).
rol_instancia(i11, guitarra_electrica).
rol_instancia(i12, bajo).
rol_instancia(i13, bateria).
rol_instancia(i14, voz_principal).
rol_instancia(i15, voz_principal).
rol_instancia(i16, guitarra_electrica).
rol_instancia(i17, bajo).
rol_instancia(i18, bateria).
rol_instancia(i19, bajo).
rol_instancia(i2, guitarra_electrica).
rol_instancia(i20, bajo).
rol_instancia(i21, bajo).
rol_instancia(i22, bajo).
rol_instancia(i23, bajo).
rol_instancia(i24, bajo).
rol_instancia(i25, bajo).
rol_instancia(i26, bajo).
rol_instancia(i27, bajo).
rol_instancia(i28, voz_secundaria).
rol_instancia(i29, voz_secundaria).
rol_instancia(i3, bajo).
rol_instancia(i30, bateria).
rol_instancia(i31, voz_principal).
rol_instancia(i32, saxofon).
rol_instancia(i33, armonica).
rol_instancia(i34, acordeon).
rol_instancia(i35, acordeon).
rol_instancia(i36, armonica).
rol_instancia(i37, voz_principal).
rol_instancia(i38, saxofon).
rol_instancia(i4, bateria).
rol_instancia(i5, piano).
rol_instancia(i6, voz_principal).
rol_instancia(i7, guitarra_electrica).
rol_instancia(i8, bajo).
rol_instancia(i9, bateria).
total_instancias_rol(38).

% --- REGLAS ---
coste_entrenamiento(A, R, 0) :- habilidad(A, R).
coste_entrenamiento(A, R, 1) :- artista(A, _), \+ habilidad(A, R).
requeridas(Rol, Cant) :- findall(1, rol_instancia(_, Rol), L), length(L, Cant).
base_saben(Rol, Cant) :- findall(A, (habilidad(A, Rol), artista(A, base)), L), length(L, Cant).
entrenamientos_necesarios(Rol, Ent) :- requeridas(Rol, Req), base_saben(Rol, Base), Temp is Req - Base, (Temp > 0 -> Ent = Temp ; Ent = 0).
entrenamientos_minimos(Total) :- setof(R, I^rol_instancia(I, R), Roles), findall(E, (member(R, Roles), entrenamientos_necesarios(R, E)), Lista), sumlist(Lista, Total).
