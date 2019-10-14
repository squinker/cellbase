/*
 * Copyright 2015-2020 OpenCB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencb.cellbase.core.common.regulatory;

// Generated Jun 5, 2012 6:41:13 PM by Hibernate Tools 3.4.0.CR1

import java.util.HashSet;
import java.util.Set;

/**
 * MirnaMature generated by hbm2java.
 */
@Deprecated
public class MirnaMature implements java.io.Serializable {

    private int mirnaMatureId;
    private String mirbaseAcc;
    private String mirbaseId;
    private String sequence;
    private Set<MirnaTarget> mirnaTargets = new HashSet<>(0);
    private Set<MirnaGeneToMature> mirnaGeneToMatures = new HashSet<>();

    public MirnaMature() {
    }

    public MirnaMature(int mirnaMatureId, String mirbaseAcc, String mirbaseId, String sequence) {
        this.mirnaMatureId = mirnaMatureId;
        this.mirbaseAcc = mirbaseAcc;
        this.mirbaseId = mirbaseId;
        this.sequence = sequence;
    }

    public MirnaMature(int mirnaMatureId, String mirbaseAcc, String mirbaseId, String sequence, Set<MirnaTarget> mirnaTargets,
                       Set<MirnaGeneToMature> mirnaGeneToMatures) {
        this.mirnaMatureId = mirnaMatureId;
        this.mirbaseAcc = mirbaseAcc;
        this.mirbaseId = mirbaseId;
        this.sequence = sequence;
        this.mirnaTargets = mirnaTargets;
        this.mirnaGeneToMatures = mirnaGeneToMatures;
    }

    public int getMirnaMatureId() {
        return this.mirnaMatureId;
    }

    public void setMirnaMatureId(int mirnaMatureId) {
        this.mirnaMatureId = mirnaMatureId;
    }

    public String getMirbaseAcc() {
        return this.mirbaseAcc;
    }

    public void setMirbaseAcc(String mirbaseAcc) {
        this.mirbaseAcc = mirbaseAcc;
    }

    public String getMirbaseId() {
        return this.mirbaseId;
    }

    public void setMirbaseId(String mirbaseId) {
        this.mirbaseId = mirbaseId;
    }

    public String getSequence() {
        return this.sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public Set<MirnaTarget> getMirnaTargets() {
        return this.mirnaTargets;
    }

    public void setMirnaTargets(Set<MirnaTarget> mirnaTargets) {
        this.mirnaTargets = mirnaTargets;
    }

    public Set<MirnaGeneToMature> getMirnaGeneToMatures() {
        return this.mirnaGeneToMatures;
    }

    public void setMirnaGeneToMatures(Set<MirnaGeneToMature> mirnaGeneToMatures) {
        this.mirnaGeneToMatures = mirnaGeneToMatures;
    }

}
