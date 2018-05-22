package compilers.ybdesire.com.pycompiler;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.res.ResourcesCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class CodeEditText extends AppCompatEditText {
    //high light key words
    private static final Pattern PATTERN_NUMBERS = Pattern.compile(
            "\\b(\\d*[.]?\\d+)\\b");
    private static final Pattern PATTERN_PREPROCESSOR = Pattern.compile(
            "^[\t ]*(#define|#undef|#if|#ifdef|#ifndef|#else|#elif|#endif|" +
                    "#error|#pragma|#extension|#version|#line|#include)\\b",
            Pattern.MULTILINE);
    private static final Pattern PATTERN_KEYWORDS = Pattern.compile(
            "\\b(" +
                    "and|del|from|not|while|as|elif|global|or|with|assert|else|if|pass|yield|break|" +
                    "except|import|print|class|exec|in|raise|continue|finally|is|return|def|for|lambda|try" +

                    ")\\b");
    private static final Pattern PATTERN_BUILTINS = Pattern.compile(
            "\\b(radians|degrees|sin|cos|tan|asin|acos|atan|pow|" +
                    "exp|log|sqrt|inversesqrt|abs|sign|floor|ceil|fract|mod|" +
                    "min|max|length|Math|System|out|printf|print|println|" +
                    "console|Arrays|Array|vector|List|list|ArrayList|Map|HashMap|" +
                    "dict|java|util|lang|import|from|in|charset|lang|href|name|" +
                    "target|onclick|onmouseover|onmouseout|accesskey|code|codebase|" +
                    "width|height|align|vspace|hspace|border|name|archive|mayscript|" +
                    "alt|shape|coords|target|nohref|size|color|face|src|loop|bgcolor|" +
                    "background|text|vlink|alink|bgproperties|topmargin|leftmargin|" +
                    "marginheight|marginwidth|onload|onunload|onfocus|onblur|stylesrc|" +
                    "scroll|clear|type|value|valign|span|compact|pluginspage|pluginurl|" +
                    "hidden|autostart|playcount|volume|controls|controller|mastersound|" +
                    "starttime|endtime|point-size|weight|action|method|enctype|onsubmit|" +
                    "onreset|scrolling|noresize|frameborder|bordercolor|cols|rows|" +
                    "framespacing|border|noshade|longdesc|ismap|usemap|lowsrc|naturalsizeflag|" +
                    "nosave|dynsrc|controls|start|suppress|maxlength|checked|language|onchange|" +
                    "onkeypress|onkeyup|onkeydown|autocomplete|prompt|for|rel|rev|media|direction|" +
                    "behaviour|scrolldelay|scrollamount|http-equiv|content|gutter|defer|event|" +
                    "multiple|readonly|cellpadding|cellspacing|rules|bordercolorlight|" +
                    "bordercolordark|summary|colspan|rowspan|nowrap|halign|disabled|accesskey|" +
                    "tabindex|id)\\b");
    private static final Pattern PATTERN_COMMENTS = Pattern.compile(
            "/\\*(?:.|[\\n\\r])*?\\*/|//.*");

    public static SpannableString setHighLight(String str)
    {
        SpannableString ss = new SpannableString(str);
        Map<Pattern,String> ptn_color = new HashMap<Pattern, String>();
        ptn_color.put(PATTERN_COMMENTS, "#076421");
        ptn_color.put(PATTERN_NUMBERS, "#c29810");
        ptn_color.put(PATTERN_KEYWORDS, "#781ebe");
        //ptn_color.put(PATTERN_BUILTINS, "#0a0733");
        ptn_color.put(PATTERN_PREPROCESSOR, "#7c4204");

        for (Pattern ptn : ptn_color.keySet())
        {
            String colorStr = ptn_color.get(ptn);

            Matcher matcher = ptn.matcher(ss);
            while (matcher.find()) {
                ss.setSpan(new ForegroundColorSpan(Color.parseColor(colorStr)), matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        return ss;
    }

    // add line number
    private Rect rect;
    private Paint paint;

    public CodeEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        rect = new Rect();
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.GRAY);
        paint.setTextSize(30);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int baseline = getBaseline();//
        for (int i = 0; i < getLineCount(); i++) {
            canvas.drawText("" + (i+1), rect.left, baseline, paint);
            baseline += getLineHeight();
        }
        super.onDraw(canvas);
    }

}
