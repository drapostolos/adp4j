package org.adp4j.core;

import java.util.regex.Pattern;

import org.adp4j.spi.FileObject;


/**
 * A file filter that consider only files, where the file name matches the 
 * given <i>regex</>. 
 * 
 */
public final class RegexFileFilter implements FileFilter{
	private final Pattern pattern;

	/**
	 * The given <i>regex</i> must be a valid regular expression as described 
	 * in {@link java.util.regex.Pattern}
	 * 
	 * @param regex
	 */
	public RegexFileFilter(String regex){
		this.pattern = Pattern.compile(regex);
	}

	/** {@inheritDoc} */
	public boolean accept(FileObject file){
		return pattern.matcher(file.getName()).matches();
	}
}
