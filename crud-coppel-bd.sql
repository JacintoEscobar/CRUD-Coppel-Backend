--
-- PostgreSQL database dump
--

-- Dumped from database version 16.1
-- Dumped by pg_dump version 16.1

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: delete_poliza(integer); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.delete_poliza(IN idp integer)
    LANGUAGE plpgsql
    AS $$DECLARE existe_poliza boolean;
DECLARE cantidad_poliza integer;
DECLARE sku_poliza text;
BEGIN
	SELECT EXISTS (SELECT id_poliza FROM poliza WHERE id_poliza = idp) INTO existe_poliza;
	IF NOT existe_poliza THEN
		RAISE EXCEPTION 'No existe la póliza %', idP USING HINT = 'Verifica el id de la póliza';
	END IF;
	
	SELECT cantidad, sku INTO cantidad_poliza, sku_poliza FROM poliza WHERE id_poliza = idp;
	
	UPDATE inventario SET cantidad = cantidad + cantidad_poliza WHERE sku IN(sku_poliza);
	
	DELETE FROM poliza WHERE id_poliza = idp;
END $$;


ALTER PROCEDURE public.delete_poliza(IN idp integer) OWNER TO postgres;

--
-- Name: get_last_empleado_id(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_last_empleado_id() RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE last_id integer;
BEGIN
	SELECT MAX(id_empleado) INTO last_id FROM empleado;
	RETURN last_id;
END
$$;


ALTER FUNCTION public.get_last_empleado_id() OWNER TO postgres;

--
-- Name: get_last_poliza_id(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_last_poliza_id() RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE last_id integer;
BEGIN
	SELECT MAX(id_poliza) FROM poliza INTO last_id;
	return last_id;
END
$$;


ALTER FUNCTION public.get_last_poliza_id() OWNER TO postgres;

--
-- Name: insert_empleado(text, text, text); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.insert_empleado(IN nombre text, IN apellido text, IN puesto text)
    LANGUAGE plpgsql
    AS $$DECLARE nuevo_id integer;
BEGIN
    SELECT MAX(id_empleado) + 1 FROM empleado INTO nuevo_id;
	INSERT INTO empleado VALUES (nuevo_id, nombre, apellido, puesto);
END;
$$;


ALTER PROCEDURE public.insert_empleado(IN nombre text, IN apellido text, IN puesto text) OWNER TO postgres;

--
-- Name: insert_inventario(text, text, integer); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.insert_inventario(IN s text, IN n text, IN ca integer)
    LANGUAGE plpgsql
    AS $$DECLARE existe_inventario boolean;
BEGIN
	SELECT EXISTS (SELECT sku FROM inventario WHERE sku IN(UPPER(s))) INTO existe_inventario;
	
	IF existe_inventario THEN
		RAISE EXCEPTION 'No pueden existir 2 inventarios con el mismo SKU' USING ERRCODE = '23505';
	END IF;
	
	INSERT INTO inventario VALUES(UPPER(s), n, ca);
END
$$;


ALTER PROCEDURE public.insert_inventario(IN s text, IN n text, IN ca integer) OWNER TO postgres;

--
-- Name: insert_poliza(integer, text, integer, text); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.insert_poliza(IN eg integer, IN s text, IN ca integer, IN f text)
    LANGUAGE plpgsql
    AS $$DECLARE existe_empleado boolean;
DECLARE existe_inventario boolean;
DECLARE nuevo_id_poliza integer;
DECLARE num_polizas integer;
DECLARE inventario_disponible integer;
BEGIN
	SELECT EXISTS (SELECT id_empleado FROM empleado WHERE id_empleado = eg) INTO existe_empleado;
	IF NOT existe_empleado THEN
		RAISE EXCEPTION 'No existe el empleado %', eg USING HINT = 'Verifica el id del empleado';
	END IF;
	
	SELECT EXISTS (SELECT sku FROM inventario WHERE sku IN(UPPER(s))) INTO existe_inventario;
	IF NOT existe_inventario THEN
		RAISE EXCEPTION 'No existe el inventario %', UPPER(s) USING HINT = 'Verifica el sku del inventario';
	END IF;
	
	SELECT cantidad INTO inventario_disponible FROM inventario WHERE sku IN(UPPER(s));
	IF ca > inventario_disponible THEN
		RAISE EXCEPTION 'Inventario insuficiente' USING HINT = 'Verifica la cantidad del inventario';
	END IF;
	
	SELECT COUNT(*) INTO num_polizas FROM poliza;
	
	IF num_polizas = 0 THEN
		INSERT INTO poliza VALUES (1, eg, s, ca, f);
	ELSE
		SELECT MAX(id_poliza) + 1 INTO nuevo_id_poliza FROM poliza;
		INSERT INTO poliza VALUES (nuevo_id_poliza, eg, s, ca, f);
	END IF;
	
	UPDATE inventario SET cantidad = cantidad - ca WHERE sku IN(UPPER(s));
END $$;


ALTER PROCEDURE public.insert_poliza(IN eg integer, IN s text, IN ca integer, IN f text) OWNER TO postgres;

--
-- Name: update_campos_poliza(integer, integer, text, integer); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.update_campos_poliza(IN idp integer, IN ide integer, IN s text, IN ca integer)
    LANGUAGE plpgsql
    AS $$
DECLARE existe_inventario boolean;
DECLARE existe_empleado boolean;
DECLARE id_empleado_genero integer;
DECLARE sku_poliza text;
DECLARE cantidad_inventario integer;
DECLARE cantidad_poliza integer;
BEGIN
	/*Verificamos que el inventario exista*/
	SELECT EXISTS (SELECT sku FROM inventario WHERE sku IN(UPPER(s))) INTO existe_inventario;
	IF NOT existe_inventario THEN
		RAISE EXCEPTION 'No existe el inventario %', UPPER(s) USING HINT = 'Verifica el SKU del inventario';
	END IF;
	
	/*Verificamos que el empleado exista*/
	SELECT EXISTS (SELECT id_empleado FROM empleado WHERE id_empleado = ide) INTO existe_empleado;
	IF NOT existe_empleado THEN
		RAISE EXCEPTION 'No existe el empleado %', ide USING HINT = 'Verifica el id del empleado';
	END IF;
	
	/*Verificamos si el inventario seleccionado es el mismo al de la póliza o si se seleccionó uno diferente*/
	SELECT sku INTO sku_poliza FROM poliza WHERE id_poliza = idp;
	IF NOT sku_poliza IN(UPPER(s)) THEN
		/*Verificamos que el inventario seleccionado tenga cantidad*/
		SELECT cantidad INTO cantidad_inventario FROM inventario WHERE sku IN(UPPER(s));
		IF ca > cantidad_inventario OR cantidad_inventario = 0 THEN
			RAISE EXCEPTION 'Inventario insuficiente' USING HINT = 'Verifica la cantidad de la póliza';
		END IF;
		
		/*Devolvemos la cantidad de la póliza al inventario correspondiente*/
		SELECT cantidad INTO cantidad_poliza FROM poliza WHERE id_poliza = idp;
		UPDATE inventario SET cantidad = cantidad + cantidad_poliza WHERE sku IN(sku_poliza);
		
		/*Actualizamos la cantidad del inventario seleccionado*/
		UPDATE inventario SET cantidad = cantidad - ca WHERE sku IN(UPPER(s));
		
		/*Actualizamos la cantidad y el inventario de la póliza*/
		UPDATE poliza SET sku = UPPER(s) WHERE id_poliza = idp;
	ELSE
		/*Verificamos si la nueva cantidad es mayor o menor a la cantidad de la póliza*/
		SELECT cantidad INTO cantidad_poliza FROM poliza WHERE id_poliza = idp;
		IF ca < cantidad_poliza THEN
			/*Devolvemos la diferencia de cantidad al inventario*/
			UPDATE inventario SET cantidad = cantidad + (cantidad_poliza - ca) WHERE sku IN(sku_poliza);
		ELSE
			/*Verificamos si el inventario tiene cantidad suficiente o está vacío*/
			SELECT cantidad INTO cantidad_inventario FROM inventario WHERE sku IN(sku_poliza);
			IF ca > cantidad_inventario OR cantidad_inventario = 0 THEN
				RAISE EXCEPTION 'Inventario insuficiente' USING HINT = 'Verifica la cantidad de la póliza';
			END IF;
			
			/*Al inventario se le resta la diferencia entre la nueva cantidad y la cantidad actual de la póliza*/
			UPDATE inventario SET cantidad = cantidad - (ca - cantidad_poliza) WHERE sku IN(sku_poliza);
		END IF;
	END IF;
	
	/*Actualizamos la cantidad de la póliza*/
	UPDATE poliza SET cantidad = ca, empleado_genero = ide WHERE id_poliza = idp;
END;
$$;


ALTER PROCEDURE public.update_campos_poliza(IN idp integer, IN ide integer, IN s text, IN ca integer) OWNER TO postgres;

--
-- Name: update_campos_poliza(integer, text, text, text, integer); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.update_campos_poliza(IN idp integer, IN nombre_e text, IN apellido_e text, IN s text, IN ca integer)
    LANGUAGE plpgsql
    AS $$
DECLARE existe_inventario boolean;
DECLARE id_empleado_genero integer;
DECLARE sku_poliza text;
DECLARE cantidad_inventario integer;
DECLARE cantidad_poliza integer;
BEGIN
	/*Verificamos que el inventario exista*/
	SELECT EXISTS (SELECT sku FROM inventario WHERE sku IN(UPPER(s))) INTO existe_inventario;
	IF NOT existe_inventario THEN
		RAISE EXCEPTION 'No existe el inventario %', UPPER(s) USING HINT = 'Verifica el SKU del inventario';
	END IF;
	
	/*Verificamos si el inventario seleccionado es el mismo al de la póliza o si se seleccionó uno diferente*/
	SELECT sku INTO sku_poliza FROM poliza WHERE id_poliza = idp;
	IF NOT sku_poliza IN(UPPER(s)) THEN
		/*Verificamos que el inventario seleccionado tenga cantidad*/
		SELECT cantidad INTO cantidad_inventario FROM inventario WHERE sku IN(UPPER(s));
		IF ca > cantidad_inventario OR cantidad_inventario = 0 THEN
			RAISE EXCEPTION 'Inventario insuficiente' USING HINT = 'Verifica la cantidad de la póliza';
		END IF;
		
		/*Devolvemos la cantidad de la póliza al inventario correspondiente*/
		SELECT cantidad INTO cantidad_poliza FROM poliza WHERE id_poliza = idp;
		UPDATE inventario SET cantidad = cantidad + cantidad_poliza WHERE sku IN(sku_poliza);
		
		/*Actualizamos la cantidad del inventario seleccionado*/
		UPDATE inventario SET cantidad = cantidad - ca WHERE sku IN(UPPER(s));
		
		/*Actualizamos la cantidad y el inventario de la póliza*/
		UPDATE poliza SET sku = UPPER(s) WHERE id_poliza = idp;
	ELSE
		/*Verificamos si la nueva cantidad es mayor o menor a la cantidad de la póliza*/
		SELECT cantidad INTO cantidad_poliza FROM poliza WHERE id_poliza = idp;
		IF ca < cantidad_poliza THEN
			/*Devolvemos la diferencia de cantidad al inventario*/
			UPDATE inventario SET cantidad = cantidad + (cantidad_poliza - ca) WHERE sku IN(sku_poliza);
		ELSE
			/*Verificamos si el inventario tiene cantidad suficiente o está vacío*/
			SELECT cantidad INTO cantidad_inventario FROM inventario WHERE sku IN(sku_poliza);
			IF ca > cantidad_inventario OR cantidad_inventario = 0 THEN
				RAISE EXCEPTION 'Inventario insuficiente' USING HINT = 'Verifica la cantidad de la póliza';
			END IF;
			
			/*Al inventario se le resta la diferencia entre la nueva cantidad y la cantidad actual de la póliza*/
			UPDATE inventario SET cantidad = cantidad - (ca - cantidad_poliza) WHERE sku IN(sku_poliza);
		END IF;
	END IF;
	
	/*Actualizamos la cantidad de la póliza*/
	UPDATE poliza SET cantidad = ca WHERE id_poliza = idp;
	
	/*Actualizamos la información del empleado de la póliza*/
	SELECT empleado_genero INTO id_empleado_genero FROM poliza WHERE id_poliza = idp;
	UPDATE empleado SET nombre = nombre_e, apellido = apellido_e WHERE id_empleado = id_empleado_genero;
END;
$$;


ALTER PROCEDURE public.update_campos_poliza(IN idp integer, IN nombre_e text, IN apellido_e text, IN s text, IN ca integer) OWNER TO postgres;

--
-- Name: update_empleado(integer, text, text, text); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.update_empleado(IN ide integer, IN n text, IN ap text, IN pu text)
    LANGUAGE plpgsql
    AS $$DECLARE existe_empleado boolean;
BEGIN
	SELECT EXISTS (SELECT id_empleado FROM empleado WHERE id_empleado = ide) INTO existe_empleado;
	IF NOT existe_empleado THEN
		RAISE EXCEPTION 'No existe el empleado %', ide USING HINT = 'Verifica el id del empleado';
	END IF;
	
	UPDATE empleado SET id_empleado = ide, nombre = n, apellido = ap, puesto = pu WHERE id_empleado = ide;
END
$$;


ALTER PROCEDURE public.update_empleado(IN ide integer, IN n text, IN ap text, IN pu text) OWNER TO postgres;

--
-- Name: update_inventario(text, text, integer); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.update_inventario(IN s text, IN n text, IN ca integer)
    LANGUAGE plpgsql
    AS $$DECLARE existe_inventario boolean;
BEGIN
	SELECT EXISTS (SELECT sku FROM inventario WHERE sku IN(UPPER(s))) INTO existe_inventario;
	IF NOT existe_inventario THEN
		RAISE EXCEPTION 'No existe el inventario %', UPPER(s) USING HINT = 'Verifica el sku del inventario';
	END IF;
	
	UPDATE inventario SET sku = UPPER(s), nombre = n, cantidad = ca WHERE sku IN(UPPER(s));
END
$$;


ALTER PROCEDURE public.update_inventario(IN s text, IN n text, IN ca integer) OWNER TO postgres;

--
-- Name: update_poliza(integer, integer, text, integer, text); Type: PROCEDURE; Schema: public; Owner: postgres
--

CREATE PROCEDURE public.update_poliza(IN idp integer, IN eg integer, IN s text, IN ca integer, IN f text)
    LANGUAGE plpgsql
    AS $$DECLARE existe_empleado boolean;
DECLARE existe_inventario boolean;
DECLARE cantidad_inventario integer;
DECLARE cantidad_poliza integer;
DECLARE sku_poliza text;
BEGIN
	/*Verificamos que el empleado exista*/
	SELECT EXISTS (SELECT id_empleado FROM empleado WHERE id_empleado = eg) INTO existe_empleado;
	IF NOT existe_empleado THEN
		RAISE EXCEPTION 'No existe el empleado %', eg USING HINT = 'Verifica el id del empleado';
	END IF;
	
	/*Verificamos que el inventario exista*/
	SELECT EXISTS (SELECT sku FROM inventario WHERE sku IN(UPPER(s))) INTO existe_inventario;
	IF NOT existe_inventario THEN
		RAISE EXCEPTION 'No existe el inventario %', UPPER(s) USING HINT = 'Verifica el SKU del inventario';
	END IF;
	
	/*Verificamos que el inventario con el que se actualiza la póliza tenga la cantidad seleccionada*/
	SELECT cantidad INTO cantidad_inventario FROM inventario WHERE sku IN(UPPER(s));
	IF ca > cantidad_inventario THEN
		RAISE EXCEPTION 'Inventario insuficiente' USING HINT = 'Verifica la cantidad de la póliza';
	END IF;
	
	/*En caso de que la póliza se actualice con diferente inventario, devolvemos la cantidad de la póliza al inventario*/
	SELECT sku INTO sku_poliza FROM poliza WHERE id_poliza = idp;
	IF sku_poliza != UPPER(s) THEN
		SELECT cantidad INTO cantidad_poliza FROM poliza WHERE id_poliza = idp;
		UPDATE inventario SET cantidad = cantidad + cantidad_poliza WHERE sku IN(sku_poliza);
	END IF;
	
	UPDATE poliza SET empleado_genero = eg, sku = s, cantidad = ca, fecha = f WHERE id_poliza = idp;
	
	/*Actualizamos la cantidad del inventario con el que se está actualizando la póliza*/
	UPDATE inventario SET cantidad = cantidad - ca WHERE sku IN(UPPER(s));
END;
$$;


ALTER PROCEDURE public.update_poliza(IN idp integer, IN eg integer, IN s text, IN ca integer, IN f text) OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: empleado; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.empleado (
    id_empleado integer NOT NULL,
    nombre text NOT NULL,
    apellido text NOT NULL,
    puesto text NOT NULL
);


ALTER TABLE public.empleado OWNER TO postgres;

--
-- Name: inventario; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.inventario (
    sku text NOT NULL,
    nombre text NOT NULL,
    cantidad integer NOT NULL
);


ALTER TABLE public.inventario OWNER TO postgres;

--
-- Name: inventario_disponible; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.inventario_disponible (
    cantidad integer
);


ALTER TABLE public.inventario_disponible OWNER TO postgres;

--
-- Name: poliza; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.poliza (
    id_poliza integer NOT NULL,
    empleado_genero integer NOT NULL,
    sku text NOT NULL,
    cantidad integer NOT NULL,
    fecha character varying(255) NOT NULL
);


ALTER TABLE public.poliza OWNER TO postgres;

--
-- Data for Name: empleado; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.empleado (id_empleado, nombre, apellido, puesto) FROM stdin;
3	Adolfo	Pinzón	Vendedor
4	Erick	De la Cruz	Vendedor
5	Benito	Bodoque	Vendedor
6	Guantesco	Escobar	Jefe
7	Guantesito	Escobar	Jefe
8	Juanito	Escobar	Jefe
9	Benito	Gonzales	Prueba
10	Pedro	Prueba	Jefe
2	Zuleidi cambio	De la Cruz	Vendedor
1	Jacinto	Escobar 2	Jefe
\.


--
-- Data for Name: inventario; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.inventario (sku, nombre, cantidad) FROM stdin;
321EWQ	Pantalón	10
PRUEBA1	Inventario 1	15
AS	asdas	10
TSREMA102	Blusa	21
TSWHSA103	Gorro	18
TSBLMA101	Playera	19
12QW34ER	Short	5
\.


--
-- Data for Name: inventario_disponible; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.inventario_disponible (cantidad) FROM stdin;
\.


--
-- Data for Name: poliza; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.poliza (id_poliza, empleado_genero, sku, cantidad, fecha) FROM stdin;
2	3	321EWQ	4	2020-10-10
1	2	PRUEBA1	5	2023-10-10
\.


--
-- Name: empleado empleado_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.empleado
    ADD CONSTRAINT empleado_pkey PRIMARY KEY (id_empleado);


--
-- Name: inventario inventario_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inventario
    ADD CONSTRAINT inventario_pkey PRIMARY KEY (sku);


--
-- Name: poliza poliza_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.poliza
    ADD CONSTRAINT poliza_pkey PRIMARY KEY (id_poliza);


--
-- Name: poliza fk_poliza_empleado; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.poliza
    ADD CONSTRAINT fk_poliza_empleado FOREIGN KEY (empleado_genero) REFERENCES public.empleado(id_empleado);


--
-- Name: poliza fk_poliza_inventario; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.poliza
    ADD CONSTRAINT fk_poliza_inventario FOREIGN KEY (sku) REFERENCES public.inventario(sku);


--
-- PostgreSQL database dump complete
--

