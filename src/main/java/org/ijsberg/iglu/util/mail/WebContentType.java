package org.ijsberg.iglu.util.mail;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

public enum WebContentType {

    _3DMF("3dmf", "x-world/x-3dmf"),
    JSON("JSON", "application/json"),
    ABS("abs", "audio/x-mpeg"),
    AI("ai", "application/postscript"),
    AIF("aif", "audio/x-aiff"),
    AIFC("aifc", "audio/x-aiff"),
    AIFF("aiff", "audio/x-aiff"),
    ANO("ano", "application/x-annotator"),
    ASN("asn", "application/astound"),
    ASP("asp", "application/x-asap"),
    AU("au", "audio/basic"),
    AVI("avi", "video/x-msvideo"),
    AXS("axs", "application/x-olescript"),
    BCPIO("bcpio", "application/x-bcpio"),
    BIN("bin", "application/octet-stream"),
    BMP("bmp", "image/x-ms-bmp"),
    C("c", "text/plain"),
    C_PP("c++", "text/plain"),
    CAL("cal", "image/x-cals"),
    CC("cc", "text/plain"),
    CCV("ccv", "application/ccv"),
    CGM("cgm", "image/cgm"),
    CMX("cmx", "image/x-cmx"),
    CPIO("cpio", "application/x-cpio"),
    CPP("cpp", "text/plain"),
    CSH("csh", "application/x-csh"),
    CSS("css", "text/css"),
    CSV("csv", "text/csv"),
    DIR("dir", "application/x-dirview"),
    DOC("doc", "application/vnd.framemaker"),
    DSF("dsf", "image/x-mgx-dsf"),
    DVI("dvi", "application/x-dvi"),
    DWF("dwf", "drawing/x-dwf"),
    DWG("dwg", "image/vnd.dwg"),
    DXF("dxf", "image/vnd.dxf"),
    EPS("eps", "application/postscript"),
    ES("es", "audio/echospeech"),
    EVY("evy", "application/envoy"),
    EXE("exe", "application/octet-stream"),
    FAXMGR("faxmgr", "application/x-fax-manager"),
    FAXMGRJOB("faxmgrjob", "application/x-fax-manager-job"),
    FIF("fif", "application/fractals"),
    FM("fm", "application/vnd.framemaker"),
    FRAME("frame", "application/vnd.framemaker"),
    FRM("frm", "application/x-alpha-form"),
    G3F("g3f", "image/g3fax"),
    GIF("gif", "image/gif"),
    GRAPHML("graphml", "application/graphml"),
    GTAR("gtar", "application/x-gtar"),
    H("h", "text/plain"),
    HDF("hdf", "application/hdf"),
    HQX("hqx", "application/mac-binhex40"),
    HTM("htm", "text-html"),
    HTML("html", "text/html"),
    ICE("ice", "x-conference/x-cooltalk"),
    ICNBK("icnbk", "application/x-iconbook"),
    IEF("ief", "image/ief"),
    IGS("igs", "application/iges"),
    INS("ins", "application/x-insight"),
    INSIGHT("insight", "application/x-insight"),
    INST("inst", "application/x-install"),
    IV("iv", "graphics/x-inventor"),
    JAR("jar", "application/java-archive"),
    JPE("jpe", "image/jpeg"),
    JPEG("jpeg", "image/jpeg"),
    JPG("jpg", "image/jpeg"),
    JS("js", "text/javascript"),
    TS("ts", "text/javascript"),
    JSP("jsp", "text/html"),
    LATEX("latex", "application/x-latex"),
    LCC("lcc", "application/fastman"),
    LIC("lic", "application/x-enterlicense"),
    LS("ls", "application/x-javascript"),
    MA("ma", "application/mathematica"),
    MAIL("mail", "application/x-mailfolder"),
    MAN("man", "application/x-troff-man"),
    MBD("mbd", "application/mbedlet"),
    ME("me", "application/x-troff-me"),
    MIF("mif", "application/vnd.mif"),
    MIL("mil", "image/x-cals"),
    MMID("mmid", "x-music/x-midi"),
    MOCHA("mocha", "text/vbscript"),
    MOVIE("movie", "video/x-sgi-movie"),
    MP2A("mp2a", "audio/x-mpeg-2"),
    MP2V("mp2v", "video/mpeg-2"),
    MPA("mpa", "audio/x-mpeg"),
    MPA2("mpa2", "audio/x-mpeg-2"),
    MPE("mpe", "video/mpeg"),
    MPEG("mpeg", "video/mpeg"),
    MPEGA("mpega", "audio/x-mpeg"),
    MPG("mpg", "video/mpeg"),
    MPV2("mpv2", "video/mpeg-2"),
    MS("ms", "application/x-troff-ms"),
    MSH("msh", "x-model/x-mesh"),
    ODA("oda", "application/oda"),
    ODS("ods", "application/x-oleobject"),
    OPP("opp", "x-form/x-openscape"),
    P3D("p3d", "application/x-p3d"),
    PAC("pac", "application/x-ns-proxy-autoconfig"),
    PBM("pbm", "image/x-portable-bitmap"),
    PCD("pcd", "image/x-photo-cd"),
    PCN("pcn", "application/x-pcn"),
    PDF("pdf", "application/pdf"),
    PGM("pgm", "image/x-portable-graymap"),
    PICT("pict", "image/x-pict"),
    PL("pl", "application/x-perl"),
    PNG("png", "image/x-png"),
    PNM("pnm", "image/x-portable-anymap"),
    PP("pp", "application/x-ppages"),
    PPAGES("ppages", "application/x-ppages"),
    PPM("ppm", "image/x-portable-pixmap"),
    PPT("ppt", "application/vnd.ms-powerpoint"),
    PPZ("ppz", "application/mspowerpoint"),
    PS("ps", "application/postscript"),
    QTMOV("qtmov", "video/quicktime"),
    RA("ra", "application/x-pn-realaudio"),
    RAD("rad", "application/x-rad-powermedia"),
    RAM("ram", "application/x-pn-realaudio"),
    RAS("ras", "image/x-cmu-raster"),
    RGB("rgb", "image/rgb"),
    RTF("rtf", "application/rtf"),
    SC("sc", "application/x-showcase"),
    SEA("sea", "application/x-stuffit"),
    SGI_LPR("sgi-lpr", "application/x-sgi-lpr"),
    SH("sh", "application/x-sh"),
    SHAR("shar", "application/x-shar"),
    SHO("sho", "application/x-showcase"),
    SHOW("show", "application/x-showcase"),
    SHOWCASE("showcase", "application/x-showcase"),
    SIT("sit", "application/x-stuffit"),
    SKP("skp", "application/vnd.koan"),
    SLIDES("slides", "application/x-showcase"),
    SND("snd", "audio/basic"),
    SPL("spl", "application/futuresplash"),
    SRC("src", "application/x-wais-source"),
    SVD("svd", "application/vnd.svd"),
    SVF("svf", "image/vnd.svf"),
    SVG("svg", "image/svg+xml"),
    SVR("svr", "x-world/x-svr"),
    T("t", "application/x-troff"),
    TALK("talk", "text/x-speech"),
    TAR("tar", "application/x-tar"),
    TARDIST("tardist", "application/x-tardist"),
    TCL("tcl", "application/x-tcl"),
    TEX("tex", "application/x-tex"),
    TEXI("texi", "application/x-texinfo"),
    TEXINFO("texinfo", "application/x-texinfo"),
    TIF("tif", "image/tiff"),
    TIFF("tiff", "image/tiff"),
    TR("tr", "application/x-troff"),
    TROFF("troff", "application/x-troff"),
    TXT("txt", "text/plain"),
    USTAR("ustar", "application/x-ustar"),
    UU("uu", "application/octet-stream"),
    V5D("v5d", "application/vis5d"),
    VDO("vdo", "video/vdo"),
    VIV("viv", "video/vnd.vivo"),
    VOX("vox", "audio/voxware"),
    VRML("vrml", "x-world/x-vrml"),
    VRW("vrw", "x-world/x-vream"),
    VTS("vts", "workbook/formulaone"),
    WAV("wav", "audio/x-wav"),
    WB("wb", "application/x-inpview"),
    WBA("wba", "application/x-webbasic"),
    WFX("wfx", "x-script/x-wfxclient"),
    WI("wi", "image/wavelet"),
    WKZ("wkz", "application/x-wingz"),
    WRL("wrl", "x-world/x-vrml"),
    WSRC("wsrc", "application/x-wais-source"),
    WVR("wvr", "x-world/x-wvr"),
    XBM("xbm", "image/x-xbitmap"),
    XML("xml", "application/xml"),
    XPM("xpm", "image/x-xpixmap"),
    XWD("xwd", "image/x-xwindowdump"),
    XLSX("xlsx","application/vnd.ms-excel"),
    ZIP("zip", "application/zip"),
    ZTARDIST("ztardist", "application/x-ztardist"),
    EVENT_STREAM("", "text/event-stream"),
    VOID("", "");

    String extension;
    String contentType;

    WebContentType(String extension, String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }

    public static TreeMap<String, WebContentType> extensionToContentType = new TreeMap<>();

    static {
        for(WebContentType contentType : WebContentType.values()) {
            extensionToContentType.put(contentType.extension, contentType);
        }
    }

    public static Set<String> filterImageExtensions() {
        HashSet<String> imageExtensions = new HashSet<>();
        for(WebContentType webContentType : WebContentType.values()) {
            if(webContentType.getContentType().startsWith("image/")) {
                imageExtensions.add(webContentType.extension);
            }
        }
        return imageExtensions;
    }
}
