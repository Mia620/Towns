package xaos.tiles.entities.living.heroes;

import xaos.skills.SkillManager;
import xaos.utils.Messages;
import xaos.utils.Utils;

import java.util.ArrayList;

public class HeroSkills {

    private final ArrayList<String> skills;
    private final ArrayList<Integer> levels;

    public HeroSkills(String sSkills, String sLevels) throws Exception {
        this.skills = Utils.getArray(sSkills);
        ArrayList<String> alLevels = Utils.getArray(sLevels);

        if (this.skills == null || alLevels == null || this.skills.isEmpty()) {
            throw new Exception(Messages.getString("HeroSkills.0")); //$NON-NLS-1$
        }

        this.levels = new ArrayList<>();

        int iLevel;
        for (String alLevel : alLevels) {
            try {
                iLevel = Integer.parseInt(alLevel);
                this.levels.add(Integer.valueOf(iLevel));
            } catch (NumberFormatException nfe) {
                throw new Exception(Messages.getString("HeroSkills.1") + alLevel + "]"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }

        if (this.skills.size() != this.levels.size()) {
            throw new Exception(Messages.getString("HeroSkills.3")); //$NON-NLS-1$
        }

        // Todo ok, miramos que las skills existan
        for (String skill : this.skills) {
            if (SkillManager.getItem(skill) == null) {
                throw new Exception(Messages.getString("HeroSkills.2") + skill + "]"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
    }

    /**
     * Retorna una lista de skills id a partir de un level concreto
     *
     * @param level
     * @return
     */
    public ArrayList<String> getSkillsWhenReachLevel(int level) {
        ArrayList<String> alSkills = null;

        for (int i = 0; i < levels.size(); i++) {
            if (levels.get(i) == level) {
                if (alSkills == null) {
                    alSkills = new ArrayList<>();
                }
                alSkills.add(skills.get(i));
            }
        }

        return alSkills;
    }
}
