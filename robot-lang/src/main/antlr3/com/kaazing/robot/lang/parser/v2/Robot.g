/**
 * Copyright (c) 2007-2013, Kaazing Corporation. All rights reserved.
 */
grammar Robot;

options {
  k=5;
}

@lexer::header {
package com.kaazing.robot.lang.parser.v2;
}

@lexer::members {

    // Based on:
    //  http://antlr.1301665.n2.nabble.com/Antlr-Bug-Failed-semantic-predicate-in-lexer-triggers-endless-loop-td4550197.html
    public void reportError(RecognitionException re) {
        if (re instanceof FailedPredicateException) {
            recover(re);
        }

        super.reportError(re);
    }
}

@parser::header {
package com.kaazing.robot.lang.parser.v2;

import static com.kaazing.robot.lang.parser.v2.ParserHelper.parseHexBytes;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;
import java.util.LinkedList;
import java.nio.ByteBuffer;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import com.kaazing.robot.lang.ast.*;
import com.kaazing.robot.lang.ast.matcher.*;
import com.kaazing.robot.lang.ast.value.*;

import com.kaazing.robot.lang.el.ExpressionContext;
import com.kaazing.robot.lang.regex.NamedGroupPattern;

}

// Disable automatic error recovery
@parser::members {
    private ExpressionFactory elFactory;
    private ExpressionContext elContext;

    void setExpressionFactory(ExpressionFactory elFactory) {
        this.elFactory = elFactory;
    }

    void setExpressionContext(ExpressionContext elContext) {
        this.elContext = elContext;
    }

    private void validateExpressionSetup() {
        if (elFactory == null) {
            throw new IllegalStateException("ExpressionFactory is null");
        }

        if (elContext == null) {
            throw new IllegalStateException("ExpressionContext is null");
        }
    }

    @Override
    public boolean mismatchIsMissingToken(IntStream input, BitSet follow) {
        return false;
    }

    @Override
    public Object recoverFromMismatchedToken(IntStream input,
                                             int ttype,
                                             BitSet follow)
        throws RecognitionException {

        if (mismatchIsUnwantedToken(input, ttype)) {
            throw new UnwantedTokenException(ttype, input);
        }

        return super.recoverFromMismatchedToken(input, ttype, follow);
    }

    @Override
    public Object recoverFromMismatchedSet(IntStream input,
                                           RecognitionException re,
                                           BitSet follow)
        throws RecognitionException {

        throw re;
    } 
}

// Throw the RecognitionException in our catch clauses
@rulecatch {
  catch (RecognitionException re) {
    throw re;
  }
}

/* Parser rules */

// A robot script is comprised of a list of commands, events and barriers for
// each stream. Note that we deliberately allow empty scripts, in order to
// handle scripts which are comprised of nothing but empty lines and comments.

scriptNode returns [AstScriptNode value]
    : { $value = new AstScriptNode(); }
      ( stream=streamNode { $value.getStreams().add($stream.value); } )* EOF
    ;

streamNode returns [AstStreamNode value]
    : accept=acceptNode
      { $value = $accept.value; }
    | acceptable=acceptableNode
      { $value = $acceptable.value; }
    | connect=connectNode
      { $value = $connect.value; }
    ;
    
acceptNode returns [AstAcceptNode value]
    : a=AcceptKeyword acceptURI=URILiteral
      { $value = new AstAcceptNode();
        $value.setLocationInfo($a.getLine(), $a.getCharPositionInLine());
        $value.setLocation(URI.create($acceptURI.text));
      }
      ( AsKeyword text=Name { $value.setAcceptName($text.text); } )?
      ( streamable=serverStreamableNode
       { $value.getStreamables().add($streamable.value); } )*
    ;

acceptableNode returns [AstAcceptableNode value]
    : a=AcceptedKeyword
      { $value = new AstAcceptableNode();
        $value.setLocationInfo($a.getLine(), $a.getCharPositionInLine());
      }
      ( text=Name { $value.setAcceptName($text.text); } )?
      ( streamable=streamableNode
        { $value.getStreamables().add($streamable.value); } )+
    ;

connectNode returns [AstConnectNode value]
    : c=ConnectKeyword connectURI=URILiteral
      { $value = new AstConnectNode();
        $value.setLocationInfo($c.getLine(), $c.getCharPositionInLine());
        $value.setLocation(URI.create($connectURI.text));
      }
      ( streamable=streamableNode
        { $value.getStreamables().add($streamable.value); } )+
    ;

serverStreamableNode returns [AstStreamableNode value]
    : barrier=barrierNode 
      { $value = $barrier.value; }
    | event=serverEventNode
      { $value = $event.value; }
    | command=serverCommandNode
      { $value = $command.value; }
    ;

serverCommandNode returns [AstCommandNode value]
    : unbind=unbindNode
      { $value = $unbind.value; }
    | close=closeNode
      { $value = $close.value; }
    ;
    
serverEventNode returns [AstEventNode value]
    : opened=openedNode
      { $value = $opened.value; }
    | bound=boundNode
      { $value = $bound.value; }
    | childOpened=childOpenedNode
      { $value = $childOpened.value; }
    | childClosed=childClosedNode
      { $value = $childClosed.value; }
    | unbound=unboundNode
      { $value = $unbound.value; }
    | closed=closedNode
      { $value = $closed.value; }
    ;
    
streamableNode returns [AstStreamableNode value]
    : barrier=barrierNode
      { $value = $barrier.value; }
    | event=eventNode
      { $value = $event.value; }
    | command=commandNode
      { $value = $command.value; }
    ;

commandNode returns [AstCommandNode value]
    : write=writeNode
      { $value = $write.value; }
    | close=closeNode
      { $value = $close.value; }
    | writeHttpHeader=writeHttpHeaderNode
      { $value = $writeHttpHeader.value; }
    | writeHttpContentLength=writeHttpContentLengthNode
      { $value = $writeHttpContentLength.value; }
    | writeHttpMethod=writeHttpMethodNode
      { $value = $writeHttpMethod.value; }
    | writeHttpParameter=writeHttpParameterNode
      { $value = $writeHttpParameter.value; }
    | writeHttpVersion=writeHttpVersionNode
      { $value = $writeHttpVersion.value; }
    | writeHttpStatus=writeHttpStatusNode
      { $value = $writeHttpStatus.value; }
    | closeHttpRequest=closeHttpRequestNode
      { $value = $closeHttpRequest.value; }
    | closeHttpResponse=closeHttpResponseNode
      { $value = $closeHttpResponse.value; }
    ;
    
eventNode returns [AstEventNode value]
    : bound=boundNode
      { $value = $bound.value; }
    | closed=closedNode
      { $value = $closed.value; }
    | disconnected=disconnectedNode
      { $value = $disconnected.value; }
    | connected=connectedNode
      { $value = $connected.value; }
    | opened=openedNode
      { $value = $opened.value; }
    | read=readNode
      { $value = $read.value; }
    | unbound=unboundNode
      { $value = $unbound.value; }
    | readHttpHeader=readHttpHeaderNode
      { $value = $readHttpHeader.value; }
    | readHttpMethod=readHttpMethodNode
      { $value = $readHttpMethod.value; }
    | readHttpParameter=readHttpParameterNode
      { $value = $readHttpParameter.value; }
    | readHttpVersion=readHttpVersionNode
      { $value = $readHttpVersion.value; }
    | readHttpStatus=readHttpStatusNode
      { $value = $readHttpStatus.value; }
    ;
    
barrierNode returns [AstBarrierNode value]
    : ra=readAwaitNode
      { $value = $ra.value; }
    | rn=readNotifyNode
      { $value = $rn.value; }
    | wa=writeAwaitNode
      { $value = $wa.value; }
    | wn=writeNotifyNode
      { $value = $wn.value; }
    ;

closeNode returns [AstCloseNode value]
    : k=CloseKeyword
      { $value = new AstCloseNode();
        $value.setLocationInfo($k.getLine(), $k.getCharPositionInLine());
      }
    ;

disconnectNode returns [AstDisconnectNode value]
    : k=DisconnectKeyword
      { $value = new AstDisconnectNode();
        $value.setLocationInfo($k.getLine(), $k.getCharPositionInLine());
      }
    ;

unbindNode returns [AstUnbindNode value]
    : k=UnbindKeyword
      { $value = new AstUnbindNode();
        $value.setLocationInfo($k.getLine(), $k.getCharPositionInLine());
      }
    ;

writeNode returns [AstWriteValueNode value]
    : k=WriteKeyword v=writeValueList
      { $value = new AstWriteValueNode();
        $value.setLocationInfo($k.getLine(), $k.getCharPositionInLine());
            $value.setValues($v.value);
      }
    ;

childOpenedNode returns [AstChildOpenedNode value]
    : k=ChildKeyword OpenedKeyword
      { $value = new AstChildOpenedNode();
        $value.setLocationInfo($k.getLine(), $k.getCharPositionInLine());
      }
    ;

childClosedNode returns [AstChildClosedNode value]
    : k=ChildKeyword ClosedKeyword
      { $value = new AstChildClosedNode();
        $value.setLocationInfo($k.getLine(), $k.getCharPositionInLine());
      }
    ;

boundNode returns [AstBoundNode value]
    : k=BoundKeyword
      { $value = new AstBoundNode();
        $value.setLocationInfo($k.getLine(), $k.getCharPositionInLine());
      }
    ;

closedNode returns [AstClosedNode value]
    : k=ClosedKeyword
      { $value = new AstClosedNode(); 
        $value.setLocationInfo($k.getLine(), $k.getCharPositionInLine());
      }
    ;

connectedNode returns [AstConnectedNode value]
    : k=ConnectedKeyword
      { $value = new AstConnectedNode();
        $value.setLocationInfo($k.getLine(), $k.getCharPositionInLine());
      }
    ;

disconnectedNode returns [AstDisconnectedNode value]
    : k=DisconnectedKeyword
      { $value = new AstDisconnectedNode();
        $value.setLocationInfo($k.getLine(), $k.getCharPositionInLine());
      }
    ;

openedNode returns [AstOpenedNode value]
    : k=OpenedKeyword
      { $value = new AstOpenedNode();
        $value.setLocationInfo($k.getLine(), $k.getCharPositionInLine());
      }
    ;

readNode returns [AstReadValueNode value]
    : k=ReadKeyword m=matcherList
      { $value = new AstReadValueNode();
        $value.setLocationInfo($k.getLine(), $k.getCharPositionInLine());
        $value.setMatchers($m.value);
      }
    ;

unboundNode returns [AstUnboundNode value]
    : k=UnboundKeyword
      { $value = new AstUnboundNode();
        $value.setLocationInfo($k.getLine(), $k.getCharPositionInLine());
      }
    ;

readAwaitNode returns [AstReadAwaitNode value]
    : k=ReadKeyword AwaitKeyword b=Name
      { $value = new AstReadAwaitNode();
        $value.setLocationInfo($k.getLine(), $k.getCharPositionInLine());
        $value.setBarrierName($b.text);
      }
    ;

readNotifyNode returns [AstReadNotifyNode value]
    : k=ReadKeyword NotifyKeyword b=Name
      { $value = new AstReadNotifyNode();
        $value.setLocationInfo($k.getLine(), $k.getCharPositionInLine());
        $value.setBarrierName($b.text);
      }
    ;
    
writeNotifyNode returns [AstWriteNotifyNode value]
    : k=WriteKeyword NotifyKeyword b=Name
      { $value = new AstWriteNotifyNode();
        $value.setLocationInfo($k.getLine(), $k.getCharPositionInLine());
        $value.setBarrierName($b.text);
      }
    ;

readHttpHeaderNode returns [AstReadHttpHeaderNode value]
	: k=ReadKeyword HttpHeaderKeyword headerName=literalText headerValue=matcher
      { $value = new AstReadHttpHeaderNode();
        $value.setLocationInfo($k.getLine(), $k.getCharPositionInLine());
        $value.setName($headerName.value);
        $value.setValue($headerValue.value);
      }
	;

writeHttpHeaderNode returns [AstWriteHttpHeaderNode value]
	: k=WriteKeyword HttpHeaderKeyword headerName=literalText headerValue=writeValue
      { $value = new AstWriteHttpHeaderNode();
        $value.setLocationInfo($k.getLine(), $k.getCharPositionInLine());
        $value.setName($headerName.value);
        $value.setValue($headerValue.value);
      }
	;

writeHttpContentLengthNode returns [AstWriteHttpContentLengthNode value]
	: k=WriteKeyword HttpContentLengthKeyword
	  { $value = new AstWriteHttpContentLengthNode();
	    $value.setLocationInfo($k.getLine(), $k.getCharPositionInLine());
	  }
	;

readHttpMethodNode returns [AstReadHttpMethodNode value]
	: k=ReadKeyword HttpMethodKeyword methodText=matcher
      { $value = new AstReadHttpMethodNode();
        $value.setLocationInfo($k.getLine(), $k.getCharPositionInLine());
        $value.setMethod($methodText.value);
      }
	;

writeHttpMethodNode returns [AstWriteHttpMethodNode value]
	: k=WriteKeyword HttpMethodKeyword methodText=writeValue
      { $value = new AstWriteHttpMethodNode();
        $value.setLocationInfo($k.getLine(), $k.getCharPositionInLine());
        $value.setMethod($methodText.value);
      }
	;

readHttpParameterNode returns [AstReadHttpParameterNode value]
	: k=ReadKeyword HttpParameterKeyword keyText=literalText valueText=matcher
      { $value = new AstReadHttpParameterNode();
        $value.setLocationInfo($k.getLine(), $k.getCharPositionInLine());
        $value.setKey($keyText.value);
        $value.setValue($valueText.value);
      }
	;

writeHttpParameterNode returns [AstWriteHttpParameterNode value]
	: k=WriteKeyword HttpParameterKeyword keyText=literalText valueText=writeValue
      { $value = new AstWriteHttpParameterNode();
        $value.setLocationInfo($k.getLine(), $k.getCharPositionInLine());
    	$value.setKey($keyText.value);
        $value.setValue($valueText.value);
      }
	;

readHttpVersionNode returns [AstReadHttpVersionNode value]
	: k=ReadKeyword HttpVersionKeyword versionText=matcher
      { $value = new AstReadHttpVersionNode();
        $value.setLocationInfo($k.getLine(), $k.getCharPositionInLine());
        $value.setVersion($versionText.value);
      }
	;

writeHttpVersionNode returns [AstWriteHttpVersionNode value]
	: k=WriteKeyword HttpVersionKeyword versionText=writeValue
      { $value = new AstWriteHttpVersionNode();
        $value.setLocationInfo($k.getLine(), $k.getCharPositionInLine());
        $value.setVersion($versionText.value);
      }
	;

readHttpStatusNode returns [AstReadHttpStatusNode value]
	: k=ReadKeyword HttpStatusKeyword codeText=matcher reasonText=matcher
      { $value = new AstReadHttpStatusNode();
        $value.setLocationInfo($k.getLine(), $k.getCharPositionInLine());
        $value.setCode($codeText.value);
        $value.setReason($reasonText.value);
      }
	;

writeHttpStatusNode returns [AstWriteHttpStatusNode value]
	: k=WriteKeyword HttpStatusKeyword codeText=writeValue reasonText=writeValue
      { $value = new AstWriteHttpStatusNode();
        $value.setLocationInfo($k.getLine(), $k.getCharPositionInLine());
    	$value.setCode($codeText.value);
        $value.setReason($reasonText.value);
      }
	;

closeHttpRequestNode returns [AstCloseHttpRequestNode value]
	: k=CloseKeyword HttpRequestKeyword
      { $value = new AstCloseHttpRequestNode();
        $value.setLocationInfo($k.getLine(), $k.getCharPositionInLine());
      }
	;

closeHttpResponseNode returns [AstCloseHttpResponseNode value]
	: k=CloseKeyword HttpResponseKeyword
      { $value = new AstCloseHttpResponseNode();
        $value.setLocationInfo($k.getLine(), $k.getCharPositionInLine());
      }
	;

writeAwaitNode returns [AstWriteAwaitNode value]
    : k=WriteKeyword AwaitKeyword b=Name
      { $value = new AstWriteAwaitNode();
        $value.setLocationInfo($k.getLine(), $k.getCharPositionInLine());
        $value.setBarrierName($b.text);
      }
    ;

matcherList returns [List<AstValueMatcher> value]
    : { $value  = new LinkedList<AstValueMatcher>(); }
      ( m=matcher
          { $value.add($m.value); }
      )+
    ;

matcher returns [AstValueMatcher value]
        : text=exactTextMatcher
      { $value = $text.value; }
    | bytes=exactBytesMatcher
      { $value = $bytes.value; }
    | regex=regexMatcher
      { $value = $regex.value; }
    | expression=expressionMatcher
      { $value = $expression.value; }
    | fixed=fixedLengthBytesMatcher
      { $value = $fixed.value; }
    | variable=variableLengthBytesMatcher
      { $value = $variable.value; }
    ;

exactTextMatcher returns [AstExactTextMatcher value]
    : text=TextLiteral
      { $value = new AstExactTextMatcher($text.text.substring(1, $text.text.length()-1)); }
    ;

exactBytesMatcher returns [AstExactBytesMatcher value]
    : bytes=BytesLiteral
      { $value = new AstExactBytesMatcher(parseHexBytes($bytes.text)); }
    | byteLiteral=ByteLiteral
          {
             byte[] arr = parseHexBytes($byteLiteral.text);
             $value = new AstExactBytesMatcher( arr );
          }
    | shortLiteral=TwoByteLiteral
          {
             byte[] arr = parseHexBytes($shortLiteral.text);
             $value = new AstExactBytesMatcher( arr );
          }
    |  toWrite=signedDecimalLiteral 'L'
          {
             byte[] arr = ByteBuffer.allocate( 8 ).putLong( Long.parseLong( $toWrite.text )).array();
             $value = new AstExactBytesMatcher( arr );
          }
    |  toWrite=signedDecimalLiteral
          {
             // short for read int <number>
             byte[] arr = ByteBuffer.allocate( 4 ).putInt( Integer.parseInt( $toWrite.text )).array();
             $value = new AstExactBytesMatcher( arr );
          }
    ;

regexMatcher returns [AstRegexMatcher value]
    :  regex=RegexLiteral
       {
           $value = new AstRegexMatcher( NamedGroupPattern.compile( $regex.text ) );
       }
    ;

expressionMatcher returns [AstExpressionMatcher value]
    : expression=valueExpression[byte[\].class]
      { $value = new AstExpressionMatcher($expression.value); }
    ;
    
fixedLengthBytesMatcher returns [AstFixedLengthBytesMatcher value]
    : '[0..' lastIndex=DecimalLiteral ']'
      { 
          $value = new AstFixedLengthBytesMatcher(Integer.parseInt($lastIndex.text));
      }
    | '([0..' lastIndex=DecimalLiteral ']' capture=CaptureLiteral ')'
      { 
          $value = new AstFixedLengthBytesMatcher(Integer.parseInt($lastIndex.text), $capture.text.substring(1, $capture.text.length()));
      }

    /*
     * TODO: If I use lexer rules here for example:
     *
     *   '(' ByteKeyword capture=CaptureLiteral ')'
     *
     *   Then it will not parse "(byte:var)" It will only parse if there is a space in between, "(byte :var)". How come?
    */
    |  '(byte' capture=CaptureLiteral ')'
          {
             $value = new AstByteLengthBytesMatcher( $capture.text.substring(1, $capture.text.length()) );
          }
    |  '(short' capture=CaptureLiteral ')'
          {
             $value = new AstShortLengthBytesMatcher( $capture.text.substring(1, $capture.text.length())  );
          }
    |  '(int' capture=CaptureLiteral ')'
          {
             $value = new AstIntLengthBytesMatcher(  $capture.text.substring(1, $capture.text.length())  );
          }
    |  '(long' capture=CaptureLiteral ')'
          {
             $value = new AstLongLengthBytesMatcher( $capture.text.substring(1, $capture.text.length())  );
          }
    ;
    
variableLengthBytesMatcher returns [AstVariableLengthBytesMatcher value]
    : '[0..' length=valueExpression[Integer.class] ']'
      { $value = new AstVariableLengthBytesMatcher($length.value); }
    | '([0..' length=valueExpression[Integer.class] ']' capture=CaptureLiteral ')'
      { $value = new AstVariableLengthBytesMatcher($length.value, $capture.text.substring(1, $capture.text.length())); }
    ;

writeValueList returns [List<AstValue> value]
    : { $value  = new LinkedList<AstValue>(); }
      ( m=writeValue
          { $value.add($m.value); }
      )+
    ;
    
writeValue returns [AstValue value]
    : text=literalText
      { $value = $text.value; }
    | bytes=literalBytes
      { $value = $bytes.value; }
    | expression=expressionValue
      { $value = $expression.value; }
    ;

literalText returns [AstLiteralTextValue value]
    : text=TextLiteral
      { $value = new AstLiteralTextValue($text.text.substring(1, $text.text.length()-1)); }
    ;

literalBytes returns [AstLiteralBytesValue value]
    : bytes=BytesLiteral
      { $value = new AstLiteralBytesValue(parseHexBytes($bytes.text)); }
    ;

expressionValue returns [AstExpressionValue value]
    : expression=valueExpression[byte[\].class]
      { $value = new AstExpressionValue($expression.value); }
    ;
    
valueExpression[Class<?> expectedType] returns [ValueExpression value]
    : expression=ExpressionLiteral
      { validateExpressionSetup();
          $value = elFactory.createValueExpression(elContext, $expression.text, $expectedType); }
    ;

signedDecimalLiteral
    :  Plus DecimalLiteral
    |  Minus DecimalLiteral
    |  DecimalLiteral
    ;

ShortKeyword
    : 'short'
    ;

IntKeyword
    : 'int'
    ;

ByteKeyword
    : 'byte'
    ;

LongKeyword
    : 'long'
    ;

AcceptKeyword
    : 'accept'
    ;

AcceptedKeyword
    : 'accepted'
    ;

AsKeyword
    : 'as'
    ;

AwaitKeyword
    : 'await'
    ;

BindKeyword
    : 'bind'
    ;

BoundKeyword
    : 'bound'
    ;

ChildKeyword
    : 'child'
    ;

CloseKeyword
    : 'close'
    ;

ClosedKeyword
    : 'closed'
    ;

ConnectKeyword
    : 'connect'
    ;

ConnectedKeyword
    : 'connected'
    ;

DisconnectKeyword
    : 'disconnect'
    ;

DisconnectedKeyword
    : 'disconnected'
    ;

NotifyKeyword
    : 'notify'
    ;

OpenedKeyword
    : 'opened'
    ;

ReadKeyword
    : 'read'
    ;

UnbindKeyword
    : 'unbind'
    ;

UnboundKeyword
    : 'unbound'
    ;

WriteKeyword
    : 'write'
    ;

HttpHeaderKeyword
	: 'header'
	;
	
HttpContentLengthKeyword
	: 'content-length'
	;

HttpMethodKeyword
	: 'method'
	;

HttpParameterKeyword
	: 'parameter'
	;

HttpVersionKeyword
	: 'version'
	;

HttpStatusKeyword
	: 'status'
	;

HttpResponseKeyword
	: 'response'
	;

HttpRequestKeyword
	: 'request'
	;

// URI cannot begin with any of our data type delimiters, and MUST contain a colon.
URILiteral
    : Letter (Letter | '+')+ ':'
      (Letter | ':' | '/' | '=' | '.' | DecimalLiteral | '?' | '%' | '-' | ',' | '*')+
//      ~('"' | '/' | ']' | '}')
    ;

CaptureLiteral
    : ':' Identifier
    ;

ExpressionLiteral
    : '${' ~('}' | '\r' | '\n')+ '}'
    ;

RegexLiteral
  : '/' PatternLiteral '/' ( RegexNamedGroups+ '/' )?
  ;

RegexNamedGroups
  :  '(' CaptureLiteral RegexNamedGroups* ')'
  ;

fragment
PatternLiteral
    : (~('/' | '\r' | '\n') | '\\' '/')+
    ;

BytesLiteral
    : '[' (' ')? (ByteLiteral (' ')?)+ ']'
    ;

ByteLiteral
    : HexPrefix HexDigit HexDigit
    ;

TwoByteLiteral
    : HexPrefix HexDigit HexDigit HexDigit HexDigit
    ;

fragment
HexLiteral
    : HexPrefix HexDigit+
    ;

fragment
HexPrefix
    : '0'  ('x' | 'X')
    ;

fragment
HexDigit
    : (Digit | 'a'..'f' | 'A'..'F')
    ;


Plus
    : '+'
    ;

Minus
    : '-'
    ;

DecimalLiteral
    : Number
    ;

fragment
Number
   : Digit+
   ;

fragment
Digit
    : '0'..'9'
    ;

TextLiteral
    : '"' (EscapeSequence | ~('\\' | '[' | ']' | '\r' | '\n' | '"'))+ '"'
    ;
    
fragment
EscapeSequence
    : '\\' ('b' | 'f' | 'r' | 'n' | 't' | '\"' | '\'' | '\\')
    ;

Name
    : Identifier
    ;

fragment
Identifier
    : Letter (Digit | Letter)*
    ;

fragment
Letter
    : '\u0024'
    | '\u0041'..'\u005a'
    | '\u005f'
    | '\u0061'..'\u007a'
    | '\u00c0'..'\u00d6'
    | '\u00d8'..'\u00f6'
    | '\u00f8'..'\u00ff'
    | '\u0100'..'\u1fff'
    | '\u3040'..'\u318f'
    | '\u3300'..'\u337f'
    | '\u3400'..'\u3d2d'
    | '\u4e00'..'\u9fff'
    | '\uf900'..'\ufaff'
    ;

// Keep the whitespace, rather than skipping it, so that we can serialize
// the AST out to a pretty robot script, if need be
WS    : (' ' | '\r' | '\n' | '\t' | '\u000C')+
      { $channel = HIDDEN; }
    ;

LineComment
    : '#' ~('\n' | '\r')* '\r'? '\n'
      { $channel = HIDDEN; }
    ;
