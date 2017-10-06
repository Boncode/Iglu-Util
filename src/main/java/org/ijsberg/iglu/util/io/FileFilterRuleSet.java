/*
 * Copyright 2011-2014 Jeroen Meetsma - IJsberg Automatisering BV
 *
 * This file is part of Iglu.
 *
 * Iglu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Iglu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Iglu.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.ijsberg.iglu.util.io;

import org.ijsberg.iglu.util.collection.ArraySupport;
import org.ijsberg.iglu.util.formatting.PatternMatchingSupport;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/*
Wat wil ik hier nu eigenlijk?

selecteer files op basis van naam en/of inhoud


 */


/**
 * Contains up to 4 rules that a file may match.
 *
 * Masks are wildcard expressions as defined in Iglu's pattern matching support.
 *
 * @see PatternMatchingSupport#valueMatchesWildcardExpression(String, String)
 */
public class FileFilterRuleSet implements Cloneable, Serializable {

//	private String baseDir = null;
    private String includeFilesWithNameMask;
	private String excludeFilesWithNameMask = "";
	private String[] includeFilesContainingText = new String[0];
	private String[] excludeFilesContainingText = new String[0];


	public FileFilterRuleSet() {
		super();
	}

	private String getComparableFileName(File file) {
		String fileName = FileSupport.convertToUnixStylePath(file.getPath());
		return fileName;
	}

	private String getComparableFileName(ZipEntry zipEntry) {
		String retval = FileSupport.convertToUnixStylePath(zipEntry.getName());

	/*	if(baseDir != null) {
			if(retval.startsWith(baseDir)) {
				retval = retval.substring(baseDir.length());
			}
		}*/
		return retval;
	}

	private boolean includeBecauseOfInBaseDir(String fileName) {
/*		if(baseDir != null) {
			String unixStyleFileName = FileSupport.convertToUnixStylePath(fileName);
			System.out.println("[" + baseDir + "]" + fileName + " ===> " + unixStyleFileName.startsWith(baseDir));
			return unixStyleFileName.startsWith(baseDir);
		}*/
		return true;
	}


	private FileFilterRuleSet(String includeFilesWithNameMask, String excludeFilesWithNameMask,
			String[] includeFilesContainingLineMask, String[] excludeFilesContainingLineMask, String baseDir) {
		super();
		this.includeFilesWithNameMask = includeFilesWithNameMask;
		this.excludeFilesWithNameMask = excludeFilesWithNameMask;
		this.includeFilesContainingText = includeFilesContainingLineMask;
		this.excludeFilesContainingText = excludeFilesContainingLineMask;
		//this.baseDir = baseDir;
	}


	/**
	 * Checks if file matches rules.
	 * @param file
	 * @return true if a file exists, matches include and exclude rules and is successfully parsed in case of inspection of contents
	 * @throws IOException
	 */
	public boolean fileMatchesRules(File file) {

		if(file.exists()) {
			if(includeFilesContainingText.length == 0 && excludeFilesContainingText.length == 0) {
				return fileMatchesRules(
						getComparableFileName(file));
			} else {
				return fileMatchesRules(
						getComparableFileName(file),
						file);
			}
		}
		return false;
	}

    public boolean fileMatchesRules(ZipEntry entry, ZipFile zipFile) {

		if(includeFilesContainingText.length == 0 && excludeFilesContainingText.length == 0) {
			return fileMatchesRules(
					getComparableFileName(entry));
		} else {
			return fileMatchesRules(
					getComparableFileName(entry),
					entry, zipFile);
		}
    }

    private boolean fileMatchesRules(String fileName) {

            boolean retval =
                    includeBecauseOfInBaseDir(fileName) &&
					includeBecauseOfName(fileName) &&
                            !excludeBecauseOfName(fileName);
		return retval;
    }


	private boolean fileMatchesRules(String fileName, File file) {
        try {
			if(fileMatchesRules(fileName)) {
				String fileContents = FileSupport.getTextFileFromFS(file);
				return
					(includeBecauseOfContainedTextLine(fileContents)) &&
					!excludeBecauseOfContainedTextLine(fileContents);
			}
        } catch (IOException ioe) {
            //at the moment file does not match rules
        }
        return false;
    }

	private boolean fileMatchesRules(String fileName, ZipEntry entry, ZipFile zipFile) {
		try {
			if(fileMatchesRules(fileName)) {
				String fileContents = FileSupport.getTextFileFromZip(entry.getName(), zipFile);
				return
						(includeBecauseOfContainedTextLine(fileContents)) &&
								!excludeBecauseOfContainedTextLine(fileContents);
			}
		} catch (IOException ioe) {
			//at the moment file does not match rules
		}
		return false;
	}

	private boolean includeBecauseOfName(String fileName) {

		//System.out.print(baseDir + " : ");
//		System.out.println(includeFilesWithNameMask + " <-- " + fileName);


		boolean retval = includeFilesWithNameMask == null || "*".equals(includeFilesWithNameMask) ||
				PatternMatchingSupport.valueMatchesWildcardExpression(fileName, includeFilesWithNameMask)
		//		|| PatternMatchingSupport.valueMatchesWildcardExpression(fileName, "*/" + includeFilesWithNameMask)
		;
//		System.out.println(retval);
		return retval;
	}

	private boolean excludeBecauseOfName(String fileName) {
		boolean retval = excludeFilesWithNameMask != null && !"".equals(excludeFilesWithNameMask) &&
                (PatternMatchingSupport.valueMatchesWildcardExpression(fileName, excludeFilesWithNameMask)
		//		|| PatternMatchingSupport.valueMatchesWildcardExpression(fileName, "*/" + excludeFilesWithNameMask)
				);

		return retval;
	}

	private boolean includeBecauseOfContainedTextLine(String fileContents) throws IOException {
		boolean retval = includeFilesContainingText == null ||
                includeFilesContainingText.length == 0 ||
                occurenceFound(fileContents, includeFilesContainingText);
		return retval;
	}
	
	private boolean excludeBecauseOfContainedTextLine(String fileContents) throws IOException {
		boolean retval = excludeFilesContainingText != null &&
                excludeFilesContainingText.length > 0 &&
                occurenceFound(fileContents, excludeFilesContainingText);
		return retval;
	}


    private static boolean occurenceFound(String fileContents, String[] expressions) throws IOException {

        for(String expression : expressions) {
            if(fileContents.contains(expression)) {
                return true;
            }
        }
        return false;
    }

	public FileFilterRuleSet setIncludeFilesWithNameMask(String includeFilesWithNameMask) {

		if(includeFilesWithNameMask != null && includeFilesWithNameMask.startsWith("/")) {
			this.includeFilesWithNameMask = includeFilesWithNameMask.substring(1);
		} else {
			this.includeFilesWithNameMask = includeFilesWithNameMask;
		}
		return this;
	}


	public FileFilterRuleSet setExcludeFilesWithNameMask(String excludeFilesWithNameMask) {

		if(excludeFilesWithNameMask != null && excludeFilesWithNameMask.startsWith("/")) {
			this.excludeFilesWithNameMask = excludeFilesWithNameMask.substring(1);
		} else {
			this.excludeFilesWithNameMask = excludeFilesWithNameMask;
		}
		return this;
	}


	public FileFilterRuleSet setIncludeFilesContainingText(
            String ... includeFilesContainingText) {
		this.includeFilesContainingText = includeFilesContainingText;
		return this;
	}


	public FileFilterRuleSet setExcludeFilesContainingText(
            String ... excludeFilesContainingText) {
		this.excludeFilesContainingText = excludeFilesContainingText;
		return this;
	}

	public String getBaseDir() {
		return null;
	}

	@Override
	public FileFilterRuleSet clone() {
		return new FileFilterRuleSet(includeFilesWithNameMask, excludeFilesWithNameMask, includeFilesContainingText, excludeFilesContainingText, null);
	}
	

    public String toString() {
        return "file filter:\n" +
//				"base directory: " + baseDir + "\n" +
                "include names: " + includeFilesWithNameMask + "\n" +
                "include lines containing: " + ArraySupport.format("\"", "\"", includeFilesContainingText, ",") + "\n" +
                "exclude names: " + excludeFilesWithNameMask + "\n" +
                "exclude lines containing: " + ArraySupport.format("\"", "\"", excludeFilesContainingText, ", ") + "\n";
    }
	

}
