use pg_commandorv1;

--------------------------------------------------------------------------------------------------
------- On réécrit NotifySrvApp pour pointer vers le bon serveur + pb d'encodage
--------------------------------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS NotifySrvApp;
GO

CREATE PROCEDURE NotifySrvApp (@event VARCHAR(100), @eventData VARCHAR(1000))
AS
DECLARE @url VARCHAR(1000), @token uniqueidentifier;

--SET @url = 'http://10.201.0.16/jwas/jwservlet13?task=WTDbNotif&xml=<question><q methode="notifyDbChanged" event="' + @event + '" data="' + @eventData + '"/></question>'
SET @url = 'http://10.201.0.16/jwas/jwservlet13?task=WTDbNotif&xml=%3Cquestion%3E%3Cq%20methode=%22notifyDbChanged%22%20event=%22' + @event + '%22%20data=%22' + @eventData + '%22/%3E%3C/question%3E'

EXEC usp_AsyncExecInvoke @procedureName = N'HTTP_Request', @p1 = @url, @n1 = N'@sUrl', @token = @token output;
GO

--------------------------------------------------------------------------------------------------
------- On réécrit HTTP_Request pour passer en plan-text (déjà encodé)
--------------------------------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS HTTP_Request;
GO

CREATE PROCEDURE HTTP_Request (@sUrl VARCHAR(1000))
    AS
DECLARE @obj INT, @hr INT, @msg VARCHAR(255)

EXEC @hr = sp_OACreate 'MSXML2.ServerXMLHttp', @obj OUT
if @hr <> 0 BEGIN Raiserror('sp_OACreate MSXML2.ServerXMLHttp.3.0 failed', 16,1) RETURN END

EXEC @hr = sp_OAMethod @obj, 'open', NULL, 'POST', @sUrl, FALSE
if @hr <>0 BEGIN SET @msg = 'sp_OAMethod Open failed' GOTO eh END

--EXEC @hr = sp_OAMethod @obj, 'setRequestHeader', NULL, 'Content-Type','application/x-www-form-urlencoded'
EXEC @hr = sp_OAMethod @obj, 'setRequestHeader', NULL, 'Content-Type','application/plain-text'
if @hr <> 0 BEGIN SET @msg = 'sp_OAMethod setRequestHeader failed' GOTO eh END

EXEC @hr = sp_OAMethod @obj, send, NULL, ''
if @hr <> 0 BEGIN SET @msg = 'sp_OAMethod Send failed' GOTO eh END

EXEC @hr = sp_OADestroy @obj
RETURN

eh:
    EXEC @hr = sp_OADestroy @obj
	RETURN
GO

--------------------------------------------------------------------------------------------------
------- Reconfiguration de la base pour activer le Ole Automation si besoin
--------------------------------------------------------------------------------------------------
sp_configure 'show advanced options', 1
GO
RECONFIGURE;
GO
sp_configure 'Ole Automation Procedures', 1
GO
RECONFIGURE;
GO

--------------------------------------------------------------------------------------------------
------- Exécution de l'init de la base pour activer le Service Broker si besoin
--------------------------------------------------------------------------------------------------
DECLARE	@return_value INT
EXEC @return_value = init_database @trig = 1
SELECT 'Return Value' = @return_value
GO
