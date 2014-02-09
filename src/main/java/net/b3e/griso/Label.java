/*
 *  Copyright 2014 Christoph Böhme
 *
 *  Licensed under the Apache License, Version 2.0 the "License";
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package net.b3e.griso;

/**
 * A label
 *
 * @author Christoph Böhme
 *
 */
final class Label {

	private final Type type;
	private final int value;

	/**
	 * Creates new labels.
	 */
	public static final class Factory {

		private int generatedValue = 1;

		/**
		 * Creates a label from a user-provided value.
		 *
		 * @param userValue of the class id
		 * @return a new label
		 */
		public Label create(final int userValue) {
			return new Label(Type.FIXED, userValue);
		}

		/**
		 * Creates a new label with a generated value.
		 * @return a new label
		 */
		public Label create() {
			generatedValue += 1;
			return new Label(Type.GENERATED, generatedValue);
		}

	}

	/**
	 * Type of the label.
	 */
	private enum Type { FIXED, GENERATED }

	protected Label(final Type type, final int value) {
		this.type = type;
		this.value = value;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Label other = (Label) obj;
		if (type != other.type) {
			return false;
		}
		if (value != other.value) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime;
		if (type != null) {
			result += type.hashCode();
		}
		result = prime * result + value;
		return result;
	}

	@Override
	public String toString() {
		return Integer.toString(value) + "(" + type.toString() + ")";
	}

}
