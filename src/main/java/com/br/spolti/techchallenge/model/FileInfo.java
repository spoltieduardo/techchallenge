package com.br.spolti.techchallenge.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Data
@Component
@NoArgsConstructor
public class FileInfo implements Serializable {

	private static final long serialVersionUID = -5098158762721667207L;

	private String extension;

	private int lines;

	private Integer numberOfFiles;

	public FileInfo(String extension) {
		this.extension = extension;
	}

	public void addFileNumberOfLines(int numberOfLines) {
		this.lines += numberOfLines;
		if (this.numberOfFiles == null) {
			this.numberOfFiles = 1;
		} else {
			this.numberOfFiles++;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((extension == null) ? 0 : extension.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileInfo other = (FileInfo) obj;
		if (extension == null) {
			if (other.extension != null)
				return false;
		} else if (!extension.equals(other.extension)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return extension + ", lines = " + lines + ", numberOfFiles = " + numberOfFiles;
	}
}
