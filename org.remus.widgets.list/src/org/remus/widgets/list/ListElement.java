/*******************************************************************************
 * Copyright (c) 2012 Tom Seidel, Remus Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *
 * Contributors:
 *     Tom Seidel - initial API and implementation
 *******************************************************************************/
package org.remus.widgets.list;

/**
 * @author Tom
 *
 */
public class ListElement {
	
	private String header;
	
	private String content;
	
	private String footer;
	
	private String imageUrl;
	
	private int imageWidth;
	
	private int imageHeight;
	
	private String id;
	
	private String timeLabel;
	
	private String timeTooltip;

	
	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFooter() {
		return footer;
	}

	public void setFooter(String footer) {
		this.footer = footer;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTimeLabel() {
		return timeLabel;
	}

	public void setTimeLabel(String timeLabel) {
		this.timeLabel = timeLabel;
	}

	public String getTimeTooltip() {
		return timeTooltip;
	}

	public void setTimeTooltip(String timeTooltip) {
		this.timeTooltip = timeTooltip;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		ListElement other = (ListElement) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ListElement [header=" + header + ", content=" + content
				+ ", footer=" + footer + ", imageUrl=" + imageUrl
				+ ", imageWidth=" + imageWidth + ", imageHeight=" + imageHeight
				+ ", id=" + id + ", timeLabel=" + timeLabel + ", timeTooltip="
				+ timeTooltip + "]";
	}
	
	
}
