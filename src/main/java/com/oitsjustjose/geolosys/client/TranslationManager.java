package com.oitsjustjose.geolosys.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import com.oitsjustjose.geolosys.Geolosys;

import org.apache.commons.io.IOUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.Language;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TranslationManager
{
    private static TranslationManager instance;
    private HashMap<String, HashMap<String, String>> translations = new HashMap<>();

    private TranslationManager()
    {
        this.loadLanguages();
    }

    private void loadLanguages()
    {
        Geolosys.getInstance().LOGGER.info("Caching languages");
        for (Language lang : Minecraft.getMinecraft().getLanguageManager().getLanguages())
        {
            translations.put(lang.getLanguageCode(), new HashMap<>());
            // Come on Mojang, how are you going to have a language code that doesn't correspond to the language?
            if (!lang.getLanguageCode().contains("_"))
            {
                Geolosys.getInstance().LOGGER.info("Couldn't find langfile " + lang.getLanguageCode()
                        + ", looks like Mojang messed up. This language's translations may not work correctly.");
                continue;
            }
            InputStream in = Geolosys.class.getResourceAsStream("/assets/geolosys/lang/"
                    + lang.getLanguageCode().substring(0, lang.getLanguageCode().indexOf("_"))
                    + lang.getLanguageCode().substring(lang.getLanguageCode().indexOf("_")).toUpperCase() + ".lang");
            if (in == null)
            {
                continue;
            }
            try
            {
                for (String s : IOUtils.readLines(in, "utf-8"))
                {
                    if (!s.contains("="))
                    {
                        continue;
                    }
                    translations.get(lang.getLanguageCode()).put(s.substring(0, s.indexOf("=")),
                            s.substring(s.indexOf("=") + 1));
                }
            }
            catch (IOException ignored)
            {
            }
        }
        Geolosys.getInstance().LOGGER.info("Done caching languages!");
    }

    public String translate(String untranslated)
    {
        if (translations
                .containsKey(Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode()))
        {
            if (translations.get(Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode())
                    .containsKey(untranslated))
            {
                return translations
                        .get(Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode())
                        .get(untranslated);
            }
        }
        return translations.get("en_us").get(untranslated);
    }

    public static void init()
    {
        instance = new TranslationManager();
    }

    public static TranslationManager getInstance()
    {
        return instance;
    }
}
